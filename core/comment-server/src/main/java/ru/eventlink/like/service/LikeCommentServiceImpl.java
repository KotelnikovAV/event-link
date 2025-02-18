package ru.eventlink.like.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.eventlink.client.user.UserClient;
import ru.eventlink.configuration.LikeCommentConfig;
import ru.eventlink.dto.user.UserDto;
import ru.eventlink.exception.NotFoundException;
import ru.eventlink.exception.RestrictionsViolationException;
import ru.eventlink.like.model.LikeComment;
import ru.eventlink.like.repository.LikeCommentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeCommentServiceImpl implements LikeCommentService {
    private final LikeCommentRepository likeCommentRepository;
    private final UserClient userClient;
    private final LikeCommentConfig likeCommentConfig;

    @Override
    public void addLike(ObjectId commentId, Long authorId) {
        log.info("Adding like");

        if (!userClient.getUserExists(authorId)) {
            throw new NotFoundException("User with id =" + authorId + " was not found");
        }

        if (likeCommentRepository.existsLikeCommentByCommentIdAndAuthorId(commentId, authorId)) {
            throw new RestrictionsViolationException("User " + authorId + " have already rated comment " + commentId);
        }

        LikeComment likeComment = new LikeComment();
        likeComment.setCommentId(commentId);
        likeComment.setAuthorId(authorId);

        likeCommentRepository.save(likeComment);
        log.info("Like comment added");
    }

    @Override
    public void deleteLike(ObjectId commentId, Long authorId) {
        log.info("Deleting like");

        if (!likeCommentRepository.existsLikeCommentByCommentIdAndAuthorId(commentId, authorId)) {
            throw new RestrictionsViolationException("User " + authorId + " did not rate comment " + commentId);
        }

        likeCommentRepository.deleteLikeCommentByCommentIdAndAuthorId(commentId, authorId);
        log.info("Like comment deleted");
    }

    @Override
    public List<UserDto> findLikesByCommentId(Long userId, String commentId, int page) {
        log.info("Getting likes comment");

        if (!userClient.getUserExists(userId)) {
            throw new NotFoundException("User with id =" + userId + " was not found. Only authorized users can view " +
                    "information about users who have liked it.");
        }

        ObjectId id = new ObjectId(commentId);

        PageRequest pageRequest = PageRequest.of(page,
                likeCommentConfig.getMaxLikesListView(),
                Sort.by(Sort.Direction.ASC, "creationDate"));
        List<LikeComment> likesComment = likeCommentRepository.findLikeCommentByCommentId(id, pageRequest).getContent();

        List<Long> usersId = likesComment.stream()
                .map(LikeComment::getAuthorId)
                .toList();
        List<UserDto> users = userClient.getAllUsers(usersId, 0, usersId.size());

        log.info("The likes of the comments have been received");
        return users;
    }

    @Override
    public Long getUserIdForComment(ObjectId commentId, List<Long> usersId) {
        log.info("Getting user id for comment");

        LikeComment likeComment = likeCommentRepository.findFirstByCommentIdAndAuthorIdNotIn(commentId, usersId);

        log.info("Like comment found");
        return likeComment.getAuthorId();
    }
}
