package ru.eventlink.user_action.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.eventlink.user_action.model.UserAction;
import ru.eventlink.user_action.model.UserActionId;

import java.util.List;

public interface UserActionRepository extends JpaRepository<UserAction, UserActionId> {

    @Query("select ua.userActionId.eventId " +
            "from UserAction as ua " +
            "where ua = :userId " +
            "group by ua.actionDate " +
            "order by ua.actionDate desc " +
            "limit :limit ")
    List<Long> findEventsIdByUserIdOrderByActionDateDesc(@Param("userId") Long userId, @Param("limit") int limit);

    @Query("select ua.userActionId.eventId " +
            "from UserAction as ua " +
            "where ua.userActionId.userId = :userId ")
    List<Long> findEventsIdByUserId(@Param("userId") Long userId);

    @Query("select ua " +
            "from UserAction as ua " +
            "where ua.userActionId.eventId in :eventsId ")
    List<UserAction> findUserActionByEventIdIn(@Param("eventsId") List<Long> eventsId);
}