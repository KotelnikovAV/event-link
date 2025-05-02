package ru.eventlink.comment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.eventlink.client.RestClient;
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
    private final RestClient restClient;

    public CommentPublicServiceImpl(CommentRepository commentRepository,
                                    CommentMapper commentMapper,
                                    RestClient restClient) {
        super(commentRepository, commentMapper);
        this.restClient = restClient;
    }

    @Override
    public List<CommentDto> findAllCommentsByEventId(Long eventId, CommentSort commentSort, int page, int size) {
        log.info("Finding all comments by event id {}", eventId);

        restClient.checkUserAndEventExists(null, eventId);

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

        List<UserDto> usersDto = restClient.getAllUsers(usersId, 0, usersId.size());

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
