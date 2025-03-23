package ru.eventlink.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.eventlink.enums.State;
import ru.eventlink.event.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event>, CustomEventRepository {
    @EntityGraph(attributePaths = {"category", "location"})
    Optional<Event> findByIdAndState(Long id, State state);

    @EntityGraph(attributePaths = {"category", "location"})
    List<Event> findAllByIdIn(List<Long> ids);

    @EntityGraph(attributePaths = {"category", "location"})
    List<Event> findAllByCategoryId(Long categoryId);

    boolean existsByIdAndInitiatorId(Long id, Long initiatorId);

    @Override
    @EntityGraph(attributePaths = {"category", "location"})
    Optional<Event> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"category", "location"})
    Page<Event> findAll(Pageable pageable);
}
