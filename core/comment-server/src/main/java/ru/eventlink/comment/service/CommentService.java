package ru.eventlink.comment.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.eventlink.client.event.EventClient;
import ru.eventlink.client.user.UserClient;
import ru.eventlink.comment.mapper.CommentMapper;
import ru.eventlink.comment.repository.CommentRepository;
import ru.eventlink.enums.CommentSort;
import ru.eventlink.exception.NotFoundException;

public abstract class CommentService {
    protected final CommentRepository commentRepository;
    protected final CommentMapper commentMapper;
    protected final UserClient userClient;
    protected final EventClient eventClient;

    public CommentService(CommentRepository commentRepository,
                          CommentMapper commentMapper,
                          UserClient userClient,
                          EventClient eventClient) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.userClient = userClient;
        this.eventClient = eventClient;
    }

    protected void checkUserAndEventExists(Long userId, Long eventId) {
        if (userId != null && !userClient.getUserExists(userId)) {
            throw new NotFoundException("User with id =" + userId + " was not found");
        }

        if (eventId != null && !eventClient.findExistEventByEventId(eventId)) {
            throw new NotFoundException("Event with id =" + eventId + " was not found");
        }
    }

    protected PageRequest getPageRequest(int page, int size, CommentSort commentSort) {
        return switch (commentSort) {
            case LIKES -> PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likes"));
            case DATE -> PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "creationDate"));
        };
    }
}
