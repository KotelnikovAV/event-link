package ru.eventlink.event.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.eventlink.category.model.Category;
import ru.eventlink.category.repository.CategoryRepository;
import ru.eventlink.client.RecommendationsClient;
import ru.eventlink.client.StatClient;
import ru.eventlink.client.UserActionClient;
import ru.eventlink.client.requests.RequestClient;
import ru.eventlink.client.user.UserClient;
import ru.eventlink.dto.ViewStatsDto;
import ru.eventlink.dto.event.*;
import ru.eventlink.dto.requests.EventRequestStatusUpdateRequestDto;
import ru.eventlink.dto.requests.EventRequestStatusUpdateResultDto;
import ru.eventlink.dto.requests.ParticipationRequestDto;
import ru.eventlink.dto.user.UserShortDto;
import ru.eventlink.enums.EventPublicSort;
import ru.eventlink.enums.State;
import ru.eventlink.enums.StateActionAdmin;
import ru.eventlink.enums.Status;
import ru.eventlink.event.mapper.EventMapper;
import ru.eventlink.event.mapper.LocationMapper;
import ru.eventlink.event.mapper.RecommendationsMapper;
import ru.eventlink.event.model.Event;
import ru.eventlink.event.repository.EventRepository;
import ru.eventlink.exception.DataTimeException;
import ru.eventlink.exception.NotFoundException;
import ru.eventlink.exception.RestrictionsViolationException;
import ru.eventlink.stats.proto.ActionTypeProto;
import ru.eventlink.stats.proto.RecommendedEventProto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.eventlink.constants.Constants.FORMATTER;
import static ru.eventlink.event.model.QEvent.event;
import static ru.eventlink.utility.Constants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final StatClient statClient;
    private final UserClient userClient;
    private final RequestClient requestClient;
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final RecommendationsMapper recommendationsMapper;
    private final RecommendationsClient recommendationsClient;
    private final UserActionClient userActionClient;

    @Transactional
    @Override
    public EventFullDto addEvent(NewEventDto newEventDto, long userId) {
        log.info("The beginning of the process of creating a event");

        if (!userClient.getUserExists(userId)) {
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

        Event newEvent = eventMapper.newEventDtoToEvent(newEventDto);
        newEvent.setCategory(category);
        newEvent.setCreatedOn(LocalDateTime.now());
        newEvent.setInitiatorId(userId);
        newEvent.setPublishedOn(LocalDateTime.now());
        newEvent.setState(State.PENDING);
        newEvent.setConfirmedRequests(0L);
        newEvent.setLikes(0L);

        Event event = eventRepository.save(newEvent);
        EventFullDto eventFullDto = eventMapper.eventToEventFullDto(event);
        eventFullDto.setRating(0.0);

        log.info("The event has been created");
        return eventFullDto;
    }

    @Override
    public EventFullDto findEventByUserIdAndEventId(long userId, long eventId) {
        log.info("The beginning of the process of finding a event");

        if (!userClient.getUserExists(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        setRating(List.of(event));

        EventFullDto eventFullDto = eventMapper.eventToEventFullDto(event);

        userActionClient.collectUserAction(eventId, userId, ActionTypeProto.ACTION_VIEW);

        log.info("The event was found");
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> findEventsByUser(long userId, int from, int size) {
        log.info("The beginning of the process of finding a events");

        if (!userClient.getUserExists(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        PageRequest pageRequest = PageRequest.of(from, size);
        BooleanExpression byUserId = event.initiatorId.eq(userId);
        Page<Event> pageEvents = eventRepository.findAll(byUserId, pageRequest);
        List<Event> events = pageEvents.getContent();
        setRating(events);

        List<EventShortDto> eventsShortDto = eventMapper.listEventToListEventShortDto(events);

        eventsShortDto.stream()
                .map(EventShortDto::getId)
                .forEach(eventId -> userActionClient
                        .collectUserAction(eventId, userId, ActionTypeProto.ACTION_VIEW));

        log.info("The events was found");
        return eventsShortDto;
    }

    @Transactional
    @Override
    public EventFullDto updateEvent(UpdateEventUserRequest updateEvent, long userId, long eventId) {
        log.info("The beginning of the process of updates a event");

        if (!userClient.getUserExists(userId)) {
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
            event.setLocation(locationMapper.locationDtoToLocation(updateEvent.getLocation()));
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
        return eventMapper.eventToEventFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> findRequestByEventId(long userId, long eventId) {
        log.info("The beginning of the process of finding a requests");

        if (!userClient.getUserExists(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        List<ParticipationRequestDto> requests = requestClient.findAllRequestsByEventId(eventId, null);

        log.info("The requests was found");
        return requests;
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResultDto updateRequestByEventId(EventRequestStatusUpdateRequestDto updateRequests,
                                                                    long userId,
                                                                    long eventId) {
        log.info("The beginning of the process of update a requests");

        if (!userClient.getUserExists(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        List<ParticipationRequestDto> confirmedRequests = requestClient
                .findAllRequestsByEventId(eventId, Status.CONFIRMED.name());

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new RestrictionsViolationException("The limit on applications for this event has been reached");
        }

        List<ParticipationRequestDto> requests = requestClient
                .findAllRequestsByRequestsId(updateRequests.getRequestIds());

        if (requests.stream()
                .map(ParticipationRequestDto::getStatus)
                .anyMatch(status -> !status.equals(Status.PENDING))) {
            throw new RestrictionsViolationException("The status can only be changed for applications that are " +
                    "in the PENDING state");
        }

        requests = requestClient.updateRequest(updateRequests.getRequestIds(), updateRequests.getStatus().name());

        if (updateRequests.getStatus().equals(Status.CONFIRMED)) {
            event.setConfirmedRequests(event.getConfirmedRequests() + updateRequests.getRequestIds().size());
        }

        log.info("The requests was updated");
        return createEventRequestStatusUpdateResult(requests);
    }

    @Override
    public List<EventShortDto> findAllPublicEvents(String text, List<Long> categories, Boolean paid,
                                                   LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                   boolean onlyAvailable, EventPublicSort sort, int from, int size) {
        log.info("The beginning of the process of finding a events by public");

        if ((rangeStart != null) && (rangeEnd != null) && (rangeStart.isAfter(rangeEnd))) {
            throw new DataTimeException("Start time after end time");
        }
        Page<Event> events;
        PageRequest pageRequest = getCustomPage(from, size, sort);
        BooleanBuilder builder = new BooleanBuilder();

        if (text != null) {
            builder.and(event.annotation.containsIgnoreCase(text.toLowerCase())
                    .or(event.description.containsIgnoreCase(text.toLowerCase())));
        }

        if (!CollectionUtils.isEmpty(categories)) {
            builder.and(event.category.id.in(categories));
        }

        if (rangeStart != null && rangeEnd != null) {
            builder.and(event.eventDate.between(rangeStart, rangeEnd));
        } else if (rangeStart == null && rangeEnd != null) {
            builder.and(event.eventDate.between(LocalDateTime.MIN, rangeEnd));
        } else if (rangeStart != null) {
            builder.and(event.eventDate.between(rangeStart, LocalDateTime.MAX));
        }

        if (onlyAvailable) {
            builder.and(event.participantLimit.eq(0L))
                    .or(event.participantLimit.gt(event.confirmedRequests));
        }

        if (builder.getValue() != null) {
            events = eventRepository.findAll(builder.getValue(), pageRequest);
        } else {
            events = eventRepository.findAll(pageRequest);
        }

        setRating(events.getContent());
        log.info("The events was found by public");
        return eventMapper.listEventToListEventShortDto(events.getContent());
    }

    @Override
    public EventFullDto findPublicEventById(long id) {
        log.info("The beginning of the process of finding a event by public");

        Event event = eventRepository.findByIdAndState(id, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));

        setRating(List.of(event));
        log.info("The event was found by public");
        return eventMapper.eventToEventFullDto(event);
    }

    @Override
    public List<EventFullDto> findAllAdminEvents(List<Long> users, State state, List<Long> categories,
                                                 LocalDateTime rangeStart, LocalDateTime rangeEnd, int from,
                                                 int size, boolean sortRating) {
        log.info("The beginning of the process of finding a events by admin");
        Page<Event> pageEvents;
        PageRequest pageRequest;

        if (sortRating) {
            pageRequest = getCustomPage(from, size, EventPublicSort.LIKES);
        } else {
            pageRequest = getCustomPage(from, size, null);
        }

        BooleanBuilder builder = new BooleanBuilder();

        if (!CollectionUtils.isEmpty(users) && !users.contains(0L)) {
            builder.and(event.initiatorId.in(users));
        }

        if (state != null) {
            builder.and(event.state.eq(state));
        }

        if (!CollectionUtils.isEmpty(categories) && !categories.contains(0L)) {
            builder.and(event.category.id.in(categories));
        }

        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new DataTimeException("Start time after end time");
            }
            builder.and(event.eventDate.between(rangeStart, rangeEnd));
        } else if (rangeStart == null && rangeEnd != null) {
            builder.and(event.eventDate.between(LocalDateTime.MIN, rangeEnd));
        } else if (rangeStart != null) {
            builder.and(event.eventDate.between(rangeStart, LocalDateTime.MAX));
        }

        if (builder.getValue() != null) {
            pageEvents = eventRepository.findAll(builder.getValue(), pageRequest);
        } else {
            pageEvents = eventRepository.findAll(pageRequest);
        }

        List<Event> events = pageEvents.getContent();
        setRating(events);
        log.info("The events was found by admin");
        return eventMapper.listEventToListEventFullDto(events);
    }

    @Transactional
    @Override
    public EventFullDto updateEventAdmin(UpdateEventAdminRequest updateEvent, long eventId) {
        log.info("The beginning of the process of updates a event by admin");

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

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
            event.setLocation(locationMapper.locationDtoToLocation(updateEvent.getLocation()));
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
            setStateByAdmin(event, updateEvent.getStateAction());
        }

        log.info("The events was update by admin");
        return eventMapper.eventToEventFullDto(event);
    }

    @Override
    public boolean findExistEventByEventIdAndInitiatorId(Long eventId, Long initiatorId) {
        log.info("The beginning of the process of finding existence events");
        return eventRepository.existsByIdAndInitiatorId(eventId, initiatorId);
    }

    @Override
    public EventFullDto findEventById(Long eventId) {
        log.info("The beginning of the process of finding state events");
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        EventFullDto eventFullDto = eventMapper.eventToEventFullDto(event);
        eventFullDto.setInitiator(new UserShortDto(event.getInitiatorId(), null));
        return eventFullDto;
    }

    @Override
    public boolean findExistEventByEventId(Long eventId) {
        log.info("The beginning of the process of finding existence events");
        return eventRepository.existsById(eventId);
    }

    @Override
    @Transactional
    public void updateRatingEvent(Long eventId, int rating) {
        log.info("The beginning of the process of updating rating");
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        event.setLikes(event.getLikes() + rating);
        log.info("The updated rating");
    }

    @Override
    public List<RecommendationsDto> findRecommendations(long userId) {
        log.info("The beginning of the process of finding recommendations");

        List<RecommendedEventProto> recommendations = recommendationsClient
                .getRecommendationsForUser(userId, MAXIMUM_SIZE_OF_THE_RECOMMENDATION_LIST);

        if (CollectionUtils.isEmpty(recommendations)) {
            throw new NotFoundException("No recommendations found");
        }

        log.info("The recommendations found");
        return recommendationsMapper.listRecommendedEventProtoToListRecommendationsDto(recommendations);
    }

    private void setStateByAdmin(Event event, StateActionAdmin stateActionAdmin) {
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1)) &&
                stateActionAdmin.equals(StateActionAdmin.PUBLISH_EVENT)) {
            throw new DataTimeException("The start date of the event to be modified must be no earlier " +
                    "than one hour from the date of publication.");
        }

        if (stateActionAdmin.equals(StateActionAdmin.PUBLISH_EVENT)) {
            if (!event.getState().equals(State.PENDING)) {
                throw new RestrictionsViolationException("An event can be published only if it is in the waiting state " +
                        "for publication");
            }
            event.setState(State.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        } else {
            if (event.getState().equals(State.PUBLISHED)) {
                throw new RestrictionsViolationException("AAn event can be rejected only if it has not been " +
                        "published yet");
            }
            event.setState(State.CANCELED);
        }

    }

    private PageRequest getCustomPage(int from, int size, EventPublicSort sort) {
        if (sort != null) {
            return switch (sort) {
                case EVENT_DATE -> PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "eventDate"));
                case VIEWS -> PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "views"));
                case LIKES -> PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "likes"));
            };
        } else {
            return PageRequest.of(from, size);
        }

    }

    private List<ViewStatsDto> getViewStats(List<Event> events) {
        List<String> url = events.stream()
                .map(event -> ACTUAL_VERSION_EVENT_SERVER + "/events/" + event.getId())
                .toList();

        Optional<List<ViewStatsDto>> viewStatsDto = Optional.ofNullable(statClient
                .findByParams(DEFAULT_SEARCH_START_DATE.format(FORMATTER),
                        LocalDateTime.now().format(FORMATTER),
                        url,
                        true)
        );
        return viewStatsDto.orElse(Collections.emptyList());
    }

    private void setRating(List<Event> events) {
        if (CollectionUtils.isEmpty(events)) {
            return;
        }

        List<Long> eventsId = events.stream()
                .map(Event::getId)
                .toList();

        List<RecommendedEventProto> ratingEvents = recommendationsClient.getInteractionsCount(eventsId);

        if (CollectionUtils.isEmpty(ratingEvents)) {
            return;
        }

        Map<Long, Double> mapEventsIdRating = ratingEvents.stream()
                .collect(Collectors.toMap(RecommendedEventProto::getEventId, RecommendedEventProto::getScore));

        events.forEach(event -> event.setRating(mapEventsIdRating.getOrDefault(event.getId(), 0.0)));
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
