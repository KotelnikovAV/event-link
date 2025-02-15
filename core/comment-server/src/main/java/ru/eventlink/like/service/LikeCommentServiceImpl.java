package ru.eventlink.like.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import ru.eventlink.exception.RestrictionsViolationException;
import ru.eventlink.like.model.LikeComment;
import ru.eventlink.like.repository.LikeCommentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeCommentServiceImpl implements LikeCommentService {
    private final LikeCommentRepository likeCommentRepository;

    @Override
    public void addLike(Long eventId, ObjectId commentId, Long authorId) {
        log.info("adding like");

        if (likeCommentRepository.existsLikeCommentByCommentIdAndAuthorId(commentId, authorId)) {
            throw new RestrictionsViolationException("User " + authorId + " have already rated comment " + commentId);
        }

        LikeComment likeComment = new LikeComment();
        likeComment.setEventId(eventId);
        likeComment.setCommentId(commentId);
        likeComment.setAuthorId(authorId);

        likeCommentRepository.save(likeComment);
    }

    @Override
    public void deleteLike(Long authorId, ObjectId commentId) {
        log.info("deleting like");

        if (!likeCommentRepository.existsLikeCommentByCommentIdAndAuthorId(commentId, authorId)) {
            throw new RestrictionsViolationException("User " + authorId + " did not rate comment " + commentId);
        }

        likeCommentRepository.deleteLikeCommentByCommentIdAndAuthorId(commentId, authorId);
    }

    @Override
    public List<LikeComment> getLikeCommentsByCommentId(ObjectId commentId) {
        log.info("getting likes comment");

        return likeCommentRepository.findLikeCommentByCommentId(commentId);
    }
}
