package ru.eventlink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.eventlink.enums.Status;
import ru.eventlink.model.Request;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RequestsRepository extends JpaRepository<Request, Long> {

    List<Request> findByEventId(long eventId);

    List<Request> findByIdIn(Set<Long> id);

    List<Request> findAllByRequesterId(long id);

    Optional<Request> findByEventIdAndRequesterId(long eventId, long requesterId);

    List<Request> findAllByStatusAndEventId(Status status, long eventId);

    boolean existsByEventIdAndRequesterIdAndStatus(long eventId, long requesterId, Status status);
}