package ru.eventlink.like.service;

import org.bson.types.ObjectId;
import ru.eventlink.dto.user.UserDto;

import java.util.List;

public interface LikeCommentService {
    void addLike(ObjectId commentId, Long authorId);

    void deleteLike(ObjectId commentId, Long authorId);

    List<UserDto> findLikesByCommentId(Long userId, String commentId, int page);

    Long getUserIdForComment(ObjectId commentId, List<Long> usersId);
}
