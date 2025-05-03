package ru.eventlink.event.service;

import com.querydsl.core.BooleanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.eventlink.client.GrpcClient;
import ru.eventlink.dto.event.EventFullDto;
import ru.eventlink.dto.event.EventShortDto;
import ru.eventlink.enums.EventPublicSort;
import ru.eventlink.enums.State;
import ru.eventlink.event.mapper.Mapper;
import ru.eventlink.event.model.Event;
import ru.eventlink.event.repository.EventRepository;
import ru.eventlink.exception.DataTimeException;
import ru.eventlink.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static ru.eventlink.event.model.QEvent.event;

@Service
@Slf4j
public class EventPublicServiceImpl extends EventService implements EventPublicService {
    private final EventRepository eventRepository;
    private final Mapper mapper;

    public EventPublicServiceImpl(GrpcClient grpcClient,
                                  EventRepository eventRepository,
                                  Mapper mapper) {
        super(grpcClient);
        this.eventRepository = eventRepository;
        this.mapper = mapper;
    }

    @Override
    public List<EventShortDto> findAllPublicEvents(String text, List<Long> categories, Boolean paid,
                                                   LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                   boolean onlyAvailable, EventPublicSort sort, int page, int size) {
        log.info("The beginning of the process of finding a events by public");

        if ((rangeStart != null) && (rangeEnd != null) && (rangeStart.isAfter(rangeEnd))) {
            throw new DataTimeException("Start time after end time");
        }
        Page<Event> events;
        PageRequest pageRequest = getCustomPage(page, size, sort);
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
            events = eventRepository.findAllWithPredicateAndPageable(builder.getValue(), pageRequest);
        } else {
            events = eventRepository.findAll(pageRequest);
        }

        setRating(events.getContent());
        log.info("The events was found by public");
        return mapper.listEventToListEventShortDto(events.getContent());
    }

    @Override
    public EventFullDto findPublicEventById(long id) {
        log.info("The beginning of the process of finding a event by public");

        Event event = eventRepository.findByIdAndState(id, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));

        setRating(List.of(event));
        log.info("The event was found by public");
        return mapper.eventToEventFullDto(event);
    }
}
