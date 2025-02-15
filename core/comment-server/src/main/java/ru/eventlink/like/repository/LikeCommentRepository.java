package ru.eventlink.like.repository;

import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.eventlink.like.model.LikeComment;

import java.util.List;

public interface LikeCommentRepository extends MongoRepository<LikeComment, ObjectId> {
    boolean existsLikeCommentByCommentIdAndAuthorId(@NotNull(message = "commentId must not be null") ObjectId commentId,
                                                    @NotNull(message = "authorId must not be null") Long authorId);

    void deleteLikeCommentByCommentIdAndAuthorId(@NotNull(message = "commentId must not be null") ObjectId commentId,
                                                 @NotNull(message = "authorId must not be null") Long authorId);

    List<LikeComment> findLikeCommentByCommentId(@NotNull(message = "commentId must not be null") ObjectId commentId);
}
