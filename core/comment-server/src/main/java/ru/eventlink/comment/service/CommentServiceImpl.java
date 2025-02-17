package ru.eventlink.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.eventlink.client.event.EventClient;
import ru.eventlink.client.user.UserClient;
import ru.eventlink.comment.mapper.CommentMapper;
import ru.eventlink.comment.model.Comment;
import ru.eventlink.comment.repository.CommentRepository;
import ru.eventlink.dto.comment.CommentDto;
import ru.eventlink.dto.comment.CommentUserDto;
import ru.eventlink.dto.comment.RequestCommentDto;
import ru.eventlink.dto.comment.UpdateCommentDto;
import ru.eventlink.enums.CommentSort;
import ru.eventlink.exception.NotFoundException;
import ru.eventlink.like.service.LikeCommentService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final LikeCommentService likeCommentService;
    private final UserClient userClient;
    private final EventClient eventClient;


    @Override
    public List<CommentDto> findAllCommentsByEventId(Long eventId, CommentSort commentSort, int page, int size) {
        log.info("Finding all comments by event id {}", eventId);

        checkUserAndEventExists(null, eventId);

        List<Comment> comments = commentRepository
                .findByEventId(eventId, getPageRequest(page, size, commentSort))
                .getContent();

        List<CommentDto> commentsDto = commentMapper.commentListToCommentDtoList(comments);
//        commentsDto.forEach(commentDto -> commentDto.setUsersLiked(likeCommentService.findLikesByCommentId()));

        return List.of();
    }

    @Override
    public List<CommentUserDto> findAllCommentsByUserId(Long userId, CommentSort commentSort, int page, int size) {
        return List.of();
    }

    @Override
    public CommentDto addComment(Long userId, Long eventId, RequestCommentDto commentDto) {
        log.info("Adding comment");

        checkUserAndEventExists(userId, eventId);

        Comment comment = commentMapper.requestCommentDtoToComment(commentDto);
        comment = commentRepository.save(comment);

        log.info("Added comment");
        return commentMapper.commentToCommentDto(comment);
    }

    @Override
    public CommentDto updateComment(Long userId, Long eventId, String commentId, UpdateCommentDto updateCommentDto) {
        log.info("Updating comment");

        checkUserAndEventExists(userId, eventId);

        ObjectId id = new ObjectId(commentId);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment " + commentId + " not found"));
        comment.setText(updateCommentDto.getText());
        commentRepository.save(comment);

        log.info("Updated comment");
        return commentMapper.commentToCommentDto(comment);
    }

    @Override
    public CommentDto addSubComment(Long userId, String parentCommentId, RequestCommentDto commentDto) {
        return null;
    }

    @Override
    public CommentDto deleteComment(Long userId, String commentId) {
        return null;
    }

    private void checkUserAndEventExists(Long userId, Long eventId) {
        if (userId != null && !userClient.getUserExists(userId)) {
            throw new NotFoundException("User with id =" + userId + " was not found");
        }

        if (eventId != null && !eventClient.findExistEventByEventId(eventId)) {
            throw new NotFoundException("Event with id =" + eventId + " was not found");
        }
    }

    private PageRequest getPageRequest(int page, int size, CommentSort commentSort) {
        return switch (commentSort) {
            case LIKES -> PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likes"));
            case DATE -> PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "creationDate"));
        };
    }
}
