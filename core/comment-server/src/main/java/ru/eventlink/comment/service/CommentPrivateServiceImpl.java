package ru.eventlink.comment.service;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.eventlink.client.RestClient;
import ru.eventlink.comment.mapper.CommentMapper;
import ru.eventlink.comment.model.Comment;
import ru.eventlink.comment.repository.CommentRepository;
import ru.eventlink.configuration.LikeCommentConfig;
import ru.eventlink.dto.comment.CommentDto;
import ru.eventlink.dto.comment.RequestCommentDto;
import ru.eventlink.dto.comment.UpdateCommentDto;
import ru.eventlink.exception.NotFoundException;
import ru.eventlink.like.service.LikeCommentService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CommentPrivateServiceImpl extends CommentService implements CommentPrivateService {
    private final LikeCommentService likeCommentService;
    private final LikeCommentConfig likeCommentConfig;   
    private final RestClient restClient;

    public CommentPrivateServiceImpl(CommentRepository commentRepository,
                                     CommentMapper commentMapper,
                                     LikeCommentService likeCommentService,
                                     LikeCommentConfig likeCommentConfig,
                                     RestClient restClient) {
        super(commentRepository, commentMapper);
        this.likeCommentService = likeCommentService;
        this.likeCommentConfig = likeCommentConfig;
        this.restClient = restClient;
    }

    @Override
    public CommentDto addComment(Long userId, Long eventId, RequestCommentDto commentDto) {
        log.info("Adding comment");

        restClient.checkUserAndEventExists(userId, eventId);

        Comment comment = commentMapper.requestCommentDtoToComment(commentDto);
        comment.setCountResponse(0);
        comment.setLikes(0);
        comment.setDeleted(false);
        comment = commentRepository.save(comment);

        log.info("Added comment");
        return commentMapper.commentToCommentDto(comment);
    }

    @Override
    public CommentDto updateComment(Long userId, String commentId, UpdateCommentDto updateCommentDto) {
        log.info("Updating comment");

        restClient.checkUserAndEventExists(userId, null);

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

        restClient.checkUserAndEventExists(userId, null);

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
    @Transactional
    public CommentDto deleteComment(Long userId, String commentId) {
        log.info("Deleting comment");

        restClient.checkUserAndEventExists(userId, null);

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

        restClient.checkUserAndEventExists(authorId, null);

        ObjectId id = new ObjectId(commentId);
        likeCommentService.addLike(id, authorId);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment " + commentId + " not found"));

        if (comment.getLikes() == 0) {
            comment.setLikes(1);
            comment.setLikedUsersId(List.of(authorId));
        } else {
            comment.setLikes(comment.getLikes() + 1);
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

        restClient.checkUserAndEventExists(authorId, null);

        ObjectId id = new ObjectId(commentId);
        likeCommentService.deleteLike(id, authorId);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment " + commentId + " not found"));
        comment.setLikes(comment.getLikes() - 1);

        if (comment.getLikedUsersId().contains(authorId)) {
            List<Long> likedUsersId = comment.getLikedUsersId();
            likedUsersId.remove(authorId);
            likedUsersId.add(likeCommentService.getUserIdForComment(id, likedUsersId));
        }

        commentRepository.save(comment);
        log.info("Deleted like");
    }
}
