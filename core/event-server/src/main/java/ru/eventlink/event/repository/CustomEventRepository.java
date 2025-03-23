package ru.eventlink.event.repository;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.eventlink.event.model.Event;

public interface CustomEventRepository {
    Page<Event> findAllWithPredicateAndPageable(Predicate predicate, Pageable pageable);
}
