package ru.eventlink.event.service;

import com.querydsl.core.BooleanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.eventlink.category.model.Category;
import ru.eventlink.category.repository.CategoryRepository;
import ru.eventlink.client.RecommendationsClient;
import ru.eventlink.dto.event.EventFullDto;
import ru.eventlink.dto.event.UpdateEventAdminRequest;
import ru.eventlink.dto.user.UserShortDto;
import ru.eventlink.enums.EventPublicSort;
import ru.eventlink.enums.State;
import ru.eventlink.enums.StateActionAdmin;
import ru.eventlink.event.mapper.EventMapper;
import ru.eventlink.event.mapper.LocationMapper;
import ru.eventlink.event.model.Event;
import ru.eventlink.event.repository.EventRepository;
import ru.eventlink.exception.DataTimeException;
import ru.eventlink.exception.NotFoundException;
import ru.eventlink.exception.RestrictionsViolationException;

import java.time.LocalDateTime;
import java.util.List;

import static ru.eventlink.event.model.QEvent.event;

@Service
@Slf4j
public class EventAdminServiceImpl extends EventService implements EventAdminService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;

    public EventAdminServiceImpl(EventRepository eventRepository,
                                 CategoryRepository categoryRepository,
                                 EventMapper eventMapper,
                                 LocationMapper locationMapper,
                                 RecommendationsClient recommendationsClient) {
        super(recommendationsClient);
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.eventMapper = eventMapper;
        this.locationMapper = locationMapper;
    }

    @Override
    public List<EventFullDto> findAllAdminEvents(List<Long> users, State state, List<Long> categories,
                                                 LocalDateTime rangeStart, LocalDateTime rangeEnd, int page,
                                                 int size, boolean sortRating) {
        log.info("The beginning of the process of finding a events by admin");
        Page<Event> pageEvents;
        PageRequest pageRequest;

        if (sortRating) {
            pageRequest = getCustomPage(page, size, EventPublicSort.LIKES);
        } else {
            pageRequest = getCustomPage(page, size, null);
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
            pageEvents = eventRepository.findAllWithPredicateAndPageable(builder.getValue(), pageRequest);
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
}
