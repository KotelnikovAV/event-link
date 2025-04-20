package ru.eventlink.comment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.eventlink.client.event.EventClient;
import ru.eventlink.client.user.UserClient;
import ru.eventlink.comment.mapper.CommentMapper;
import ru.eventlink.comment.model.Comment;
import ru.eventlink.comment.repository.CommentRepository;
import ru.eventlink.dto.comment.CommentUserDto;
import ru.eventlink.enums.CommentSort;

import java.util.List;

@Service
@Slf4j
public class CommentAdminServiceImpl extends CommentService implements CommentAdminService {

    public CommentAdminServiceImpl(CommentRepository commentRepository,
                                   CommentMapper commentMapper,
                                   UserClient userClient,
                                   EventClient eventClient) {
        super(commentRepository, commentMapper, userClient, eventClient);
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
}
