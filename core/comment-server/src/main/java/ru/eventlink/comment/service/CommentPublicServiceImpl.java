package ru.eventlink.comment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.eventlink.client.event.EventClient;
import ru.eventlink.client.user.UserClient;
import ru.eventlink.comment.mapper.CommentMapper;
import ru.eventlink.comment.model.Comment;
import ru.eventlink.comment.repository.CommentRepository;
import ru.eventlink.dto.comment.CommentDto;
import ru.eventlink.dto.user.UserDto;
import ru.eventlink.enums.CommentSort;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CommentPublicServiceImpl extends CommentService implements CommentPublicService {

    public CommentPublicServiceImpl(CommentRepository commentRepository,
                                    CommentMapper commentMapper,
                                    UserClient userClient,
                                    EventClient eventClient) {
        super(commentRepository, commentMapper, userClient, eventClient);
    }

    @Override
    public List<CommentDto> findAllCommentsByEventId(Long eventId, CommentSort commentSort, int page, int size) {
        log.info("Finding all comments by event id {}", eventId);

        checkUserAndEventExists(null, eventId);

        List<Comment> comments = commentRepository
                .findByEventId(eventId, getPageRequest(page, size, commentSort))
                .getContent();

        List<CommentDto> commentsDto = getCommentsDto(comments);

        log.info("Found {} comments by event", commentsDto.size());
        return commentsDto;
    }



    private List<CommentDto> getCommentsDto(List<Comment> comments) {
        List<Long> usersId = comments.stream()
                .map(Comment::getLikedUsersId)
                .flatMap(List::stream)
                .distinct()
                .toList();

        List<UserDto> usersDto = userClient.getAllUsers(usersId, 0, usersId.size());

        List<CommentDto> commentsDto = new ArrayList<>();

        for (Comment comment : comments) {
            CommentDto commentDto = commentMapper.commentToCommentDto(comment);
            List<UserDto> usersLiked = usersDto.stream()
                    .filter(userDto -> comment.getLikedUsersId().contains(userDto.getId()))
                    .toList();
            commentDto.setUsersLiked(usersLiked);
            commentsDto.add(commentDto);
        }

        return commentsDto;
    }
}
