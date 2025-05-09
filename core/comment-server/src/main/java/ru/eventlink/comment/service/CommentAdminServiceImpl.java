package ru.eventlink.comment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.eventlink.client.RestClient;
import ru.eventlink.comment.mapper.CommentMapper;
import ru.eventlink.comment.model.Comment;
import ru.eventlink.comment.repository.CommentRepository;
import ru.eventlink.dto.comment.CommentUserDto;
import ru.eventlink.enums.CommentSort;
import ru.eventlink.exception.NotFoundException;

import java.util.List;

@Service
@Slf4j
public class CommentAdminServiceImpl extends CommentService implements CommentAdminService {
    private final RestClient restClient;

    public CommentAdminServiceImpl(CommentRepository commentRepository,
                                   CommentMapper commentMapper,
                                   RestClient restClient) {
        super(commentRepository, commentMapper);
        this.restClient = restClient;
    }

    @Override
    public List<CommentUserDto> findAllCommentsByUserId(Long userId, CommentSort commentSort, int page, int size) {
        log.info("Finding all comments by user id {}", userId);

        if (!restClient.getUserExists(userId)) {
            throw new NotFoundException("User with id =" + userId + " was not found");
        }

        List<Comment> comments = commentRepository
                .findByAuthorId(userId, getPageRequest(page, size, commentSort))
                .getContent();

        log.info("Found {} comments by user", comments.size());

        return commentMapper.commentsToCommentsUserDto(comments);
    }
}
