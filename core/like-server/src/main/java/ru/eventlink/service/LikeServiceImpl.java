package ru.eventlink.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.eventlink.client.GrpcClient;
import ru.eventlink.client.RestClient;
import ru.eventlink.dto.event.EventFullDto;
import ru.eventlink.enums.Status;
import ru.eventlink.enums.StatusLike;
import ru.eventlink.exception.NotFoundException;
import ru.eventlink.exception.RestrictionsViolationException;
import ru.eventlink.model.Like;
import ru.eventlink.repository.LikeRepository;
import ru.eventlink.stats.proto.ActionTypeProto;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static ru.eventlink.utility.Constants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final RestClient restClient;
    private final GrpcClient grpcClient;
    private final Executor asyncExecutor = Executors.newFixedThreadPool(2);

    @Override
    @Transactional
    public EventFullDto addLike(long eventId, long userId, StatusLike statusLike) {
        log.info("The beginning of the process of adding like to an event");

        checkUserAndRequest(eventId, userId);

        EventFullDto event = restClient.findEventById(eventId);

        if (likeRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new RestrictionsViolationException("You have already rated this event");
        }

        if (event.getInitiator().getId() == userId) {
            throw new RestrictionsViolationException("The initiator of the event cannot rate himself");
        }

        Like like = new Like();
        like.setUserId(userId);
        like.setEventId(event.getId());
        like.setStatus(statusLike);
        like.setCreated(LocalDateTime.now());
        likeRepository.save(like);

        grpcClient.collectUserAction(eventId, userId, ActionTypeProto.ACTION_LIKE);

        return changeRatingUserAndEvent(event, statusLike, DIFFERENCE_RATING_BY_ADD);
    }

    @Override
    @Transactional
    public EventFullDto updateLike(long eventId, long userId, StatusLike statusLike) {
        log.info("The beginning of the process of updating like to an event");

        if (!restClient.getUserExists(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        EventFullDto event = restClient.findEventById(eventId);

        Like like = likeRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("You didn't rate this event"));

        if (like.getStatus() == statusLike) {
            throw new RestrictionsViolationException("You have already " + statusLike + " this event");
        }
        like.setStatus(statusLike);
        like.setCreated(LocalDateTime.now());
        return changeRatingUserAndEvent(event, statusLike, DIFFERENCE_RATING_BY_UPDATE);
    }

    @Override
    @Transactional
    public void deleteLike(long eventId, long userId) {
        log.info("The beginning of the process of deleting like to an event");

        if (!restClient.getUserExists(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        EventFullDto event = restClient.findEventById(eventId);

        Like like = likeRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("You didn't rate this event"));

        StatusLike statusLike = like.getStatus();
        likeRepository.delete(like);
        changeRatingUserAndEvent(event, statusLike, DIFFERENCE_RATING_BY_DELETE);

        log.info("The reaction was deleted");
    }

    private EventFullDto changeRatingUserAndEvent(EventFullDto event, StatusLike statusLike, int difference) {
        if (statusLike == StatusLike.LIKE) {
            restClient.updateRatingUser(event.getInitiator().getId(), difference);
            restClient.updateRatingEvent(event.getId(), difference);
            event.setLikes(event.getLikes() + difference);
            return event;
        } else if (statusLike == StatusLike.DISLIKE) {
            restClient.updateRatingUser(event.getInitiator().getId(), difference * -1);
            restClient.updateRatingEvent(event.getId(), difference * -1);
            event.setLikes(event.getLikes() - difference);
            return event;
        }
        return null;
    }

    private void checkUserAndRequest(long eventId, long userId) {
        CompletableFuture<Boolean> userExistsFuture = CompletableFuture.supplyAsync(
                () -> restClient.getUserExists(userId), asyncExecutor
        );

        CompletableFuture<Boolean> requestExistsFuture = CompletableFuture.supplyAsync(
                () -> restClient.findExistRequests(eventId, userId, Status.CONFIRMED), asyncExecutor
        );

        userExistsFuture.thenCombine(requestExistsFuture, (userExist, requestExist) -> {
            if (!userExist) throw new NotFoundException("User with id=" + userId + " was not found");
            if (!requestExist) throw new RestrictionsViolationException("In order to like, you must be a participant in the event");
            return null;
        }).join();
    }
}
