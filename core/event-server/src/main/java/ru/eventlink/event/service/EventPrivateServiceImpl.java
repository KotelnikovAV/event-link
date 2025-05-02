package ru.eventlink.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.eventlink.category.model.Category;
import ru.eventlink.category.repository.CategoryRepository;
import ru.eventlink.client.GrpcClient;
import ru.eventlink.client.RestClient;
import ru.eventlink.dto.event.*;
import ru.eventlink.dto.requests.EventRequestStatusUpdateRequestDto;
import ru.eventlink.dto.requests.EventRequestStatusUpdateResultDto;
import ru.eventlink.dto.requests.ParticipationRequestDto;
import ru.eventlink.enums.State;
import ru.eventlink.enums.Status;
import ru.eventlink.event.mapper.Mapper;
import ru.eventlink.event.model.Event;
import ru.eventlink.event.repository.EventRepository;
import ru.eventlink.exception.DataTimeException;
import ru.eventlink.exception.NotFoundException;
import ru.eventlink.exception.RestrictionsViolationException;
import ru.eventlink.stats.proto.ActionTypeProto;
import ru.eventlink.stats.proto.RecommendedEventProto;

import java.time.LocalDateTime;
import java.util.List;

import static ru.eventlink.event.model.QEvent.event;
import static ru.eventlink.utility.Constants.MAXIMUM_SIZE_OF_THE_RECOMMENDATION_LIST;

@Service
@Slf4j
public class EventPrivateServiceImpl extends EventService implements EventPrivateService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final Mapper mapper;
    private final RestClient restClient;

    public EventPrivateServiceImpl(EventRepository eventRepository,
                                   CategoryRepository categoryRepository,
                                   Mapper mapper,
                                   GrpcClient grpcClient,
                                   RestClient restClient) {
        super(grpcClient);
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
        this.restClient = restClient;
    }

    @Transactional
    @Override
    public EventFullDto addEvent(NewEventDto newEventDto, long userId) {
        log.info("The beginning of the process of creating a event");

        if (!restClient.getUserExists(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category with id=" + newEventDto.getCategory()
                        + " was not found"));

        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new DataTimeException("The date and time for which the event is scheduled cannot be " +
                    "earlier than two hours from the current moment");
        }

        if (newEventDto.getPaid() == null) {
            newEventDto.setPaid(false);
        }
        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }
        if (newEventDto.getParticipantLimit() == null) {
            newEventDto.setParticipantLimit(0L);
        }

        Event newEvent = mapper.newEventDtoToEvent(newEventDto);
        newEvent.setCategory(category);
        newEvent.setCreatedOn(LocalDateTime.now());
        newEvent.setInitiatorId(userId);
        newEvent.setPublishedOn(LocalDateTime.now());
        newEvent.setState(State.PENDING);
        newEvent.setConfirmedRequests(0L);
        newEvent.setLikes(0L);

        Event event = eventRepository.save(newEvent);
        EventFullDto eventFullDto = mapper.eventToEventFullDto(event);
        eventFullDto.setRating(0.0);

        log.info("The event has been created");
        return eventFullDto;
    }

    @Override
    public EventFullDto findEventByUserIdAndEventId(long userId, long eventId) {
        log.info("The beginning of the process of finding a event");

        if (!restClient.getUserExists(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        setRating(List.of(event));

        EventFullDto eventFullDto = mapper.eventToEventFullDto(event);

        grpcClient.collectUserAction(eventId, userId, ActionTypeProto.ACTION_VIEW);

        log.info("The event was found");
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> findEventsByUser(long userId, int page, int size) {
        log.info("The beginning of the process of finding a events");

        if (!restClient.getUserExists(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        BooleanExpression byUserId = event.initiatorId.eq(userId);
        Page<Event> pageEvents = eventRepository.findAllWithPredicateAndPageable(byUserId, pageRequest);
        List<Event> events = pageEvents.getContent();
        setRating(events);

        List<EventShortDto> eventsShortDto = mapper.listEventToListEventShortDto(events);

        eventsShortDto.stream()
                .map(EventShortDto::getId)
                .forEach(eventId -> grpcClient
                        .collectUserAction(eventId, userId, ActionTypeProto.ACTION_VIEW));

        log.info("The events was found");
        return eventsShortDto;
    }

    @Transactional
    @Override
    public EventFullDto updateEvent(UpdateEventUserRequest updateEvent, long userId, long eventId) {
        log.info("The beginning of the process of updates a event");

        if (!restClient.getUserExists(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getState().equals(State.PUBLISHED)) {
            throw new RestrictionsViolationException("You can only change canceled events or events in the waiting state " +
                    "for moderation");
        }

        if (updateEvent.getAnnotation() != null && !updateEvent.getAnnotation().isBlank()) {
            event.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getCategory() != null) {
            Category category = categoryRepository.findById(updateEvent.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category with id=" + updateEvent.getCategory()
                            + " was not found"));
            event.setCategory(category);
        }
        if (updateEvent.getDescription() != null && !updateEvent.getDescription().isBlank()) {
            event.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getEventDate() != null) {
            if (updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new DataTimeException("The date and time for which the event is scheduled cannot be " +
                        "earlier than two hours from the current moment");
            } else {
                event.setEventDate(updateEvent.getEventDate());
            }
        }
        if (updateEvent.getLocation() != null) {
            event.setLocation(mapper.locationDtoToLocation(updateEvent.getLocation()));
        }
        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            event.setRequestModeration(updateEvent.getRequestModeration());
        }
        if (updateEvent.getTitle() != null && !updateEvent.getTitle().isBlank()) {
            event.setTitle(updateEvent.getTitle());
        }
        if (updateEvent.getStateAction() != null) {
            switch (updateEvent.getStateAction()) {
                case CANCEL_REVIEW -> event.setState(State.CANCELED);
                case SEND_TO_REVIEW -> event.setState(State.PENDING);
            }
        }

        log.info("The events was update");
        return mapper.eventToEventFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> findRequestByEventId(long userId, long eventId) {
        log.info("The beginning of the process of finding a requests");

        if (!restClient.getUserExists(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        List<ParticipationRequestDto> requests = restClient.findAllRequestsByEventId(eventId, null);

        log.info("The requests was found");
        return requests;
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResultDto updateRequestByEventId(EventRequestStatusUpdateRequestDto updateRequests,
                                                                    long userId,
                                                                    long eventId) {
        log.info("The beginning of the process of update a requests");

        if (!restClient.getUserExists(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new RestrictionsViolationException("The limit on applications for this event has been reached");
        }

        List<ParticipationRequestDto> requests = restClient
                .findAllRequestsByRequestsId(updateRequests.getRequestIds());

        if (requests.stream()
                .map(ParticipationRequestDto::getStatus)
                .anyMatch(status -> !status.equals(Status.PENDING))) {
            throw new RestrictionsViolationException("The status can only be changed for applications that are " +
                    "in the PENDING state");
        }

        requests = restClient.updateRequest(updateRequests.getRequestIds(), updateRequests.getStatus());

        if (updateRequests.getStatus().equals(Status.CONFIRMED)) {
            event.setConfirmedRequests(event.getConfirmedRequests() + updateRequests.getRequestIds().size());
        }

        log.info("The requests was updated");
        return createEventRequestStatusUpdateResult(requests);
    }

    @Override
    public List<RecommendationsDto> findRecommendations(long userId) {
        log.info("The beginning of the process of finding recommendations");

        List<RecommendedEventProto> recommendations = grpcClient
                .getRecommendationsForUser(userId, MAXIMUM_SIZE_OF_THE_RECOMMENDATION_LIST);

        if (CollectionUtils.isEmpty(recommendations)) {
            throw new NotFoundException("No recommendations found");
        }

        log.info("The recommendations found");
        return mapper.listRecommendedEventProtoToListRecommendationsDto(recommendations);
    }

    private EventRequestStatusUpdateResultDto createEventRequestStatusUpdateResult(List<ParticipationRequestDto> requests) {
        EventRequestStatusUpdateResultDto resultDto = new EventRequestStatusUpdateResultDto();
        List<ParticipationRequestDto> confirmedRequests = requests.stream()
                .filter(r -> r.getStatus() == Status.CONFIRMED)
                .toList();
        List<ParticipationRequestDto> rejectedRequests = requests.stream()
                .filter(r -> r.getStatus() == Status.REJECTED)
                .toList();
        resultDto.setConfirmedRequests(confirmedRequests);
        resultDto.setRejectedRequests(rejectedRequests);
        return resultDto;
    }
}
