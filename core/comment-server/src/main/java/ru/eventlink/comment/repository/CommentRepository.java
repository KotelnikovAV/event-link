package ru.eventlink.comment.repository;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.eventlink.comment.model.Comment;

public interface CommentRepository extends MongoRepository<Comment, ObjectId> {
    Page<Comment> findByEventId(Long eventId, Pageable pageable);
}
