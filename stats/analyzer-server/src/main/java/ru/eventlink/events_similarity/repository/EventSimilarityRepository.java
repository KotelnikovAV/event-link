package ru.eventlink.events_similarity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.eventlink.events_similarity.model.EventSimilarity;
import ru.eventlink.events_similarity.model.EventSimilarityId;

import java.util.List;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, EventSimilarityId> {

    @Query("select es " +
            "from EventSimilarity as es " +
            "where (es.eventSimilarityId.eventA = :targetEventId or es.eventSimilarityId.eventB = :targetEventId) " +
            "and (es.eventSimilarityId.eventB not in :eventsId and es.eventSimilarityId.eventA not in :eventsId) " +
            "group by es.score " +
            "order by es.score ")
    List<EventSimilarity> findEventSimilaritiesByEventsId(@Param("targetEventId") Long targetEventId,
                                                          @Param("eventsId") List<Long> eventsId);

    @Query("select es " +
            "from EventSimilarity as es " +
            "where es.eventSimilarityId.eventA in :eventsId and es.eventSimilarityId.eventB not in :eventsId " +
            "group by es.score " +
            "order by es.score desc " +
            "limit :limit ")
    List<EventSimilarity> findSimilarEvents(@Param("eventsId") List<Long> eventsId,
                                            @Param("limit") int limit);

    @Query("select es " +
            "from EventSimilarity as es " +
            "where es.eventSimilarityId.eventA = :eventId " +
            "group by es.score " +
            "order by es.score desc " +
            "limit :limit ")
    List<EventSimilarity> findNearestNeighbors(@Param("eventId") Long eventId,
                                               @Param("limit") int limit);
}