package ru.eventlink.event.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.eventlink.event.model.Event;
import ru.eventlink.event.model.QEvent;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomEventRepositoryImpl implements CustomEventRepository {
    private final EntityManager entityManager;

    @Override
    public Page<Event> findAllWithPredicateAndPageable(Predicate predicate, Pageable pageable) {
        JPAQuery<Event> dataQuery = new JPAQuery<>(entityManager);

        dataQuery.from(QEvent.event)
                .leftJoin(QEvent.event.category).fetchJoin()
                .leftJoin(QEvent.event.location).fetchJoin()
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<Event> events = dataQuery.fetch();

        if (!events.isEmpty()) {
            JPAQuery<Long> countQuery = new JPAQuery<>(entityManager)
                    .select(QEvent.event.count())
                    .from(QEvent.event)
                    .where(predicate);

            long total = countQuery.fetchFirst();

            return new PageImpl<>(events, pageable, total);
        } else {
            return new PageImpl<>(events, pageable, 0L);
        }
    }
}
