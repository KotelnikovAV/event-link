package ru.eventlink.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.eventlink.client.event.EventClient;
import ru.eventlink.client.user.UserClient;
import ru.eventlink.comment.mapper.CommentMapper;
import ru.eventlink.comment.model.Comment;
import ru.eventlink.comment.repository.CommentRepository;
import ru.eventlink.configuration.LikeCommentConfig;
import ru.eventlink.dto.comment.CommentDto;
import ru.eventlink.dto.comment.CommentUserDto;
import ru.eventlink.dto.comment.RequestCommentDto;
import ru.eventlink.dto.comment.UpdateCommentDto;
import ru.eventlink.dto.user.UserDto;
import ru.eventlink.enums.CommentSort;
import ru.eventlink.exception.NotFoundException;
import ru.eventlink.like.service.LikeCommentService;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final LikeCommentConfig likeCommentConfig;


    @Override
    public List<CommentDto> findAllCommentsByEventId(Long eventId, CommentSort commentSort, int page, int size) {
        log.info("Finding all comments by event id {}", eventId);

        checkUserAndEventExists(null, eventId);

        List<Comment> comments = commentRepository
                .findByEventId(eventId, getPageRequest(page, size, commentSort))
                .getContent();

        List<CommentDto> commentsDto = getCommentsDto(comments);

        log.info("Found {} comments by event", commentsDto.size());
        return commentsDto;
    }

    @Override
    public List<CommentUserDto> findAllCommentsByUserId(Long userId, CommentSort commentSort, int page, int size) {
        log.info("Finding all comments by user id {}", userId);

        checkUserAndEventExists(userId, null);

        List<Comment> comments = commentRepository
                .findByAuthorId(userId, getPageRequest(page, size, commentSort))
                .getContent();

        log.info("Found {} comments by user", comments.size());

        return commentMapper.commentsToCommentsUserDto(comments);
    }

    @Override
    public CommentDto addComment(Long userId, Long eventId, RequestCommentDto commentDto) {
        log.info("Adding comment");

        checkUserAndEventExists(userId, eventId);

        Comment comment = commentMapper.requestCommentDtoToComment(commentDto);
        comment.setCountResponse(0);
        comment.setDeleted(false);
        comment = commentRepository.save(comment);

        log.info("Added comment");
        return commentMapper.commentToCommentDto(comment);
    }

    @Override
    public CommentDto updateComment(Long userId, String commentId, UpdateCommentDto updateCommentDto) {
        log.info("Updating comment");

        checkUserAndEventExists(userId, null);

        Comment comment = commentRepository.findById(new ObjectId(commentId))
                .orElseThrow(() -> new NotFoundException("Comment " + commentId + " not found"));
        comment.setText(updateCommentDto.getText());
        comment.setUpdateDate(LocalDateTime.now());
        commentRepository.save(comment);

        log.info("Updated comment");
        return commentMapper.commentToCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto addSubComment(Long userId, String parentCommentId, RequestCommentDto commentDto) {
        log.info("Adding sub comment");

        checkUserAndEventExists(userId, null);

        ObjectId id = new ObjectId(parentCommentId);

        Comment subComment = commentMapper.requestCommentDtoToComment(commentDto);
        subComment.setParentCommentId(id);
        subComment.setDeleted(false);
        subComment = commentRepository.save(subComment);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment " + parentCommentId + " not found"));
        comment.setCountResponse(comment.getCountResponse() + 1);
        commentRepository.save(comment);

        log.info("Added sub comment");
        return commentMapper.commentToCommentDto(subComment);
    }

    @Override
    public CommentDto deleteComment(Long userId, String commentId) {
        log.info("Deleting comment");

        checkUserAndEventExists(userId, null);

        Comment comment = commentRepository.findById(new ObjectId(commentId))
                .orElseThrow(() -> new NotFoundException("Comment " + commentId + " not found"));
        comment.setDeleted(true);
        comment = commentRepository.save(comment);

        log.info("Deleted comment");
        return commentMapper.commentToCommentDto(comment);
    }

    @Override
    @Transactional
    public void addLike(String commentId, Long authorId) {
        log.info("Adding like");

        checkUserAndEventExists(authorId, null);

        ObjectId id = new ObjectId(commentId);
        likeCommentService.addLike(id, authorId);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment " + commentId + " not found"));

        if (comment.getLikes() == null) {
            comment.setLikes(1L);
            comment.setLikedUsersId(List.of(authorId));
        } else {
            comment.setLikes(comment.getLikes() + 1L);
            if (comment.getLikedUsersId().size() < likeCommentConfig.getMaxLikesModalView()) {
                List<Long> likedUsersId = comment.getLikedUsersId();
                likedUsersId.add(authorId);
            }
        }

        commentRepository.save(comment);
        log.info("Added like");
    }

    @Override
    @Transactional
    public void deleteLike(String commentId, Long authorId) {
        log.info("Deleting like");

        checkUserAndEventExists(authorId, null);

        ObjectId id = new ObjectId(commentId);
        likeCommentService.deleteLike(id, authorId);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment " + commentId + " not found"));
        comment.setLikes(comment.getLikes() - 1L);

        if (comment.getLikedUsersId().contains(authorId)) {
            List<Long> likedUsersId = comment.getLikedUsersId();
            likedUsersId.remove(authorId);
            likedUsersId.add(likeCommentService.getUserIdForComment(id, likedUsersId));
        }

        commentRepository.save(comment);
        log.info("Deleted like");
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

    private List<CommentDto> getCommentsDto(List<Comment> comments) {
        List<Long> usersId = comments.stream()
                .map(Comment::getLikedUsersId)
                .flatMap(List::stream)
                .distinct()
                .toList();

        List<UserDto> usersDto = userClient.getAllUsers(usersId, 0, usersId.size());

        List<CommentDto> commentsDto = new ArrayList<>();

        for (Comment comment : comments) {
            CommentDto commentDto = commentMapper.commentToCommentDto(comment);
            List<UserDto> usersLiked = usersDto.stream()
                    .filter(userDto -> comment.getLikedUsersId().contains(userDto.getId()))
                    .toList();
            commentDto.setUsersLiked(usersLiked);
            commentsDto.add(commentDto);
        }

        return commentsDto;
    }
}
