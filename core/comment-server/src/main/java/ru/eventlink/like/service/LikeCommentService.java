package ru.eventlink.like.service;

import org.bson.types.ObjectId;
import ru.eventlink.like.model.LikeComment;

import java.util.List;

public interface LikeCommentService {
    void addLike(Long eventId, ObjectId commentId, Long authorId);

    void deleteLike(Long authorId, ObjectId commentId);

    List<LikeComment> getLikeCommentsByCommentId(ObjectId commentId);
}
