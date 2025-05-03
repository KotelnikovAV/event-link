package ru.eventlink.comment.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.eventlink.comment.mapper.CommentMapper;
import ru.eventlink.comment.repository.CommentRepository;
import ru.eventlink.enums.CommentSort;

public abstract class CommentService {
    protected final CommentRepository commentRepository;
    protected final CommentMapper commentMapper;

    public CommentService(CommentRepository commentRepository,
                          CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    protected PageRequest getPageRequest(int page, int size, CommentSort commentSort) {
        return switch (commentSort) {
            case LIKES -> PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likes"));
            case DATE -> PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "creationDate"));
        };
    }
}
