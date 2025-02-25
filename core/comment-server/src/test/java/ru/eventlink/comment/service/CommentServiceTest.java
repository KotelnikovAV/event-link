package ru.eventlink.comment.service;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.eventlink.client.event.EventClient;
import ru.eventlink.client.user.UserClient;
import ru.eventlink.comment.model.Comment;
import ru.eventlink.comment.repository.CommentRepository;
import ru.eventlink.configuration.LikeCommentConfig;
import ru.eventlink.dto.comment.CommentDto;
import ru.eventlink.dto.comment.CommentUserDto;
import ru.eventlink.dto.comment.RequestCommentDto;
import ru.eventlink.dto.comment.UpdateCommentDto;
import ru.eventlink.enums.CommentSort;
import ru.eventlink.exception.NotFoundException;
import ru.eventlink.like.model.LikeComment;
import ru.eventlink.like.repository.LikeCommentRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeCommentRepository likeCommentRepository;

    @Autowired
    private LikeCommentConfig likeCommentConfig;

    @MockBean
    private UserClient userClient;

    @MockBean
    private EventClient eventClient;

    private static final Long USER_ID = 1L;

    private static final Long EVENT_ID = 1L;

    private static List<Comment> comments;

    @Container
    @ServiceConnection
    public static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:6.0"));

    private static RequestCommentDto requestCommentDto;

    private static UpdateCommentDto updateCommentDto;

    @BeforeAll
    public static void setUp() throws InterruptedException {
        requestCommentDto = RequestCommentDto.builder()
                .eventId(EVENT_ID)
                .authorId(USER_ID)
                .text("test")
                .build();

        updateCommentDto = UpdateCommentDto.builder()
                .text("This is a updated comment")
                .build();

        comments = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            Comment comment = new Comment();
            comment.setEventId(EVENT_ID);
            comment.setAuthorId((long) i);
            comment.setText("test " + i);
            comment.setCountResponse(0);
            comment.setLikes(i * 2);
            comment.setCreationDate(LocalDateTime.now());
            comment.setUpdateDate(LocalDateTime.now());
            comment.setDeleted(false);
            comments.add(comment);
            Thread.sleep(100);
        }
    }

    @AfterEach
    public void cleanUp() {
        commentRepository.deleteAll();
        likeCommentRepository.deleteAll();
    }

    @Test
    public void testAddValidComment() {
        when(userClient.getUserExists(anyLong()))
                .thenReturn(true);
        when(eventClient.findExistEventByEventId(anyLong()))
                .thenReturn(true);

        CommentDto commentDto = commentService.addComment(USER_ID, EVENT_ID, requestCommentDto);
        assertThat(commentDto.getId(), notNullValue());
        assertThat(commentDto.getCreationDate(), notNullValue());
        assertThat(commentDto.getUpdateDate(), notNullValue());
    }

    @Test
    public void testAddNotValidUserComment() {
        when(userClient.getUserExists(anyLong()))
                .thenReturn(false);
        when(eventClient.findExistEventByEventId(anyLong()))
                .thenReturn(true);

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> commentService.addComment(USER_ID, EVENT_ID, requestCommentDto))
                .withMessage("User with id =" + USER_ID + " was not found");
    }

    @Test
    public void testAddNotValidEventComment() {
        when(userClient.getUserExists(anyLong()))
                .thenReturn(true);
        when(eventClient.findExistEventByEventId(anyLong()))
                .thenReturn(false);

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> commentService.addComment(USER_ID, EVENT_ID, requestCommentDto))
                .withMessage("Event with id =" + EVENT_ID + " was not found");
    }

    @Test
    public void testFindAllCommentsByEventIdAndSortDate() {
        commentRepository.saveAll(comments);

        when(eventClient.findExistEventByEventId(anyLong()))
                .thenReturn(true);

        List<CommentDto> commentsDto = commentService.findAllCommentsByEventId(EVENT_ID, CommentSort.DATE, 0, 10);
        assertThat(commentsDto, notNullValue());
        assertThat(commentsDto.size(), equalTo(comments.size()));
        assertThat(commentsDto.getFirst().getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                equalTo(comments.getFirst().getCreationDate().truncatedTo(ChronoUnit.MILLIS)));
    }

    @Test
    public void testFindAllCommentsByEventIdAndSortLike() {
        commentRepository.saveAll(comments);

        when(eventClient.findExistEventByEventId(anyLong()))
                .thenReturn(true);

        List<CommentDto> commentsDto = commentService.findAllCommentsByEventId(EVENT_ID, CommentSort.LIKES, 0, 10);
        assertThat(commentsDto, notNullValue());
        assertThat(commentsDto.size(), equalTo(comments.size()));
        assertThat(commentsDto.getFirst().getLikes(), equalTo(comments.getLast().getLikes()));
        assertThat(commentsDto.getLast().getLikes(), equalTo(comments.getFirst().getLikes()));
    }

    @Test
    public void testFindAllCommentsByUserIdAndSortDate() {
        commentRepository.saveAll(comments);

        when(userClient.getUserExists(anyLong()))
                .thenReturn(true);

        List<CommentUserDto> commentsUserDto = commentService.findAllCommentsByUserId(USER_ID, CommentSort.DATE, 0, 10);
        assertThat(commentsUserDto, notNullValue());
        assertThat(commentsUserDto.size(), equalTo(1));
        assertThat(commentsUserDto.getFirst().getAuthorId(), equalTo(USER_ID));
    }

    @Test
    public void testUpdateValidComment() {
        Comment comment = commentRepository.save(comments.getFirst());

        when(userClient.getUserExists(anyLong()))
                .thenReturn(true);

        CommentDto commentDto = commentService.updateComment(USER_ID, comment.getId().toHexString(), updateCommentDto);
        assertThat(commentDto, notNullValue());
        assertThat(commentDto.getId(), equalTo(comment.getId().toHexString()));
        assertThat(commentDto.getText(), equalTo(updateCommentDto.getText()));
    }

    @Test
    public void testUpdateNotValidComment() {
        commentRepository.save(comments.getFirst());
        String notValidCommentId = "507f191e810c19729de860ea";

        when(userClient.getUserExists(anyLong()))
                .thenReturn(true);

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> commentService.updateComment(USER_ID, notValidCommentId, updateCommentDto))
                .withMessage("Comment " + notValidCommentId + " not found");
    }

    @Test
    public void testValidAddSubComment() {
        Comment comment = commentRepository.save(comments.getFirst());

        when(userClient.getUserExists(anyLong()))
                .thenReturn(true);

        CommentDto commentDto = commentService.addSubComment(USER_ID, comment.getId().toHexString(), requestCommentDto);
        Comment commentAfterResponse = commentRepository.findById(comment.getId()).orElse(null);
        Comment subComment = commentRepository.findById(new ObjectId(commentDto.getId())).orElse(null);
        assertThat(commentDto, notNullValue());
        assertThat(commentAfterResponse, notNullValue());
        assertThat(subComment, notNullValue());
        assertThat(subComment.getParentCommentId(), equalTo(commentAfterResponse.getId()));
        assertThat(commentAfterResponse.getCountResponse(), equalTo(1));
    }

    @Test
    public void testNotValidAddSubComment() {
        commentRepository.save(comments.getFirst());
        String notValidCommentId = "507f191e810c19729de860ea";

        when(userClient.getUserExists(anyLong()))
                .thenReturn(true);

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> commentService.addSubComment(USER_ID, notValidCommentId, requestCommentDto))
                .withMessage("Comment " + notValidCommentId + " not found");
    }

    @Test
    public void testDeleteComment() {
        Comment comment = commentRepository.save(comments.getFirst());

        when(userClient.getUserExists(anyLong()))
                .thenReturn(true);

        CommentDto commentDto = commentService.deleteComment(USER_ID, comment.getId().toHexString());
        assertThat(commentDto, notNullValue());
        assertThat(commentDto.getId(), equalTo(comment.getId().toHexString()));
        assertThat(commentDto.getDeleted(), equalTo(true));
    }

    @Test
    public void testAddLikeWhenCountLikesEqualsZero() {
        Comment commentBeforeSave = comments.getFirst();
        commentBeforeSave.setLikes(0);
        Comment comment = commentRepository.save(commentBeforeSave);

        when(userClient.getUserExists(anyLong()))
                .thenReturn(true);

        commentService.addLike(comment.getId().toHexString(), USER_ID);
        Comment commentAfterLike = commentRepository.findById(comment.getId()).orElse(null);
        LikeComment likeComment = likeCommentRepository.findAll().getFirst();
        assertThat(commentAfterLike, notNullValue());
        assertThat(commentAfterLike.getLikes(), equalTo(1));
        assertThat(commentAfterLike.getLikedUsersId().size(), equalTo(1));
        assertThat(commentAfterLike.getLikedUsersId().getFirst(), equalTo(USER_ID));
        assertThat(likeComment, notNullValue());
        assertThat(likeComment.getCommentId(), equalTo(commentAfterLike.getId()));
        assertThat(likeComment.getAuthorId(), equalTo(USER_ID));
        assertThat(likeComment.getCreationDate(), notNullValue());
    }

    @Test
    public void testAddLikeWhenCountLikesLessMaxLikesModalView() {
        Comment commentBeforeSave = comments.getFirst();
        commentBeforeSave.setLikes(0);
        Comment comment = commentRepository.save(commentBeforeSave);

        when(userClient.getUserExists(anyLong()))
                .thenReturn(true);

        for (int i = 0; i < likeCommentConfig.getMaxLikesModalView(); i++) {
            commentService.addLike(comment.getId().toHexString(), USER_ID + i);
        }

        Comment commentAfterLike = commentRepository.findById(comment.getId()).orElse(null);
        List<LikeComment> likesComment = likeCommentRepository.findAll();
        assertThat(commentAfterLike, notNullValue());
        assertThat(commentAfterLike.getLikes(), equalTo(likeCommentConfig.getMaxLikesModalView()));
        assertThat(commentAfterLike.getLikedUsersId().size(), equalTo(likeCommentConfig.getMaxLikesModalView()));
        assertThat(commentAfterLike.getLikedUsersId().getFirst(), equalTo(USER_ID));
        assertThat(commentAfterLike.getLikedUsersId().getLast(), equalTo((long)likeCommentConfig.getMaxLikesModalView()));
        assertThat(likesComment, notNullValue());
        assertThat(likesComment.size(), equalTo(likeCommentConfig.getMaxLikesModalView()));
        assertThat(likesComment.getFirst().getCommentId(), equalTo(commentAfterLike.getId()));
    }

    @Test
    public void testAddLikeWhenCountLikesEqualsOrMoreMaxLikesModalView() {
        int countLikes = likeCommentConfig.getMaxLikesModalView() + 5;
        Comment commentBeforeSave = comments.getFirst();
        commentBeforeSave.setLikes(0);
        Comment comment = commentRepository.save(commentBeforeSave);

        when(userClient.getUserExists(anyLong()))
                .thenReturn(true);

        for (int i = 0; i < countLikes; i++) {
            commentService.addLike(comment.getId().toHexString(), USER_ID + i);
        }

        Comment commentAfterLike = commentRepository.findById(comment.getId()).orElse(null);
        List<LikeComment> likesComment = likeCommentRepository.findAll();
        assertThat(commentAfterLike, notNullValue());
        assertThat(commentAfterLike.getLikes(), equalTo(countLikes));
        assertThat(commentAfterLike.getLikedUsersId().size(), equalTo(likeCommentConfig.getMaxLikesModalView()));
        assertThat(commentAfterLike.getLikedUsersId().getFirst(), equalTo(USER_ID));
        assertThat(commentAfterLike.getLikedUsersId().getLast(), equalTo((long)likeCommentConfig.getMaxLikesModalView()));
        assertThat(likesComment, notNullValue());
        assertThat(likesComment.size(), equalTo(countLikes));
        assertThat(likesComment.getFirst().getCommentId(), equalTo(commentAfterLike.getId()));
    }

    @Test
    public void testDeleteLikeWhenUsersLikeNotChanged() {
        int countLikes = likeCommentConfig.getMaxLikesModalView() + 5;
        Comment commentBeforeSave = comments.getFirst();
        commentBeforeSave.setLikes(0);
        Comment comment = commentRepository.save(commentBeforeSave);

        when(userClient.getUserExists(anyLong()))
                .thenReturn(true);

        for (int i = 0; i < countLikes; i++) {
            commentService.addLike(comment.getId().toHexString(), USER_ID + i);
        }

        List<LikeComment> likesCommentBeforeDelete = likeCommentRepository.findAll();
        Comment commentBeforeDeleteLike = commentRepository.findById(comment.getId()).orElse(null);
        commentService.deleteLike(comment.getId().toHexString(), (long) countLikes);
        Comment commentAfterDeleteLike = commentRepository.findById(comment.getId()).orElse(null);
        List<LikeComment> likesCommentAfterDelete = likeCommentRepository.findAll();
        boolean contains = likesCommentAfterDelete.stream()
                        .anyMatch(likeComment -> likeComment.getCommentId().equals(comment.getId()) &&
                                likeComment.getAuthorId().equals((long) countLikes));

        assertThat(commentBeforeDeleteLike, notNullValue());
        assertThat(commentAfterDeleteLike, notNullValue());
        assertThat(commentAfterDeleteLike.getLikes(), equalTo(commentBeforeDeleteLike.getLikes() - 1));
        assertThat(commentAfterDeleteLike.getLikedUsersId(), equalTo(commentBeforeDeleteLike.getLikedUsersId()));
        assertThat(likesCommentBeforeDelete, notNullValue());
        assertThat(likesCommentAfterDelete, notNullValue());
        assertThat(likesCommentAfterDelete.size(), equalTo(likesCommentBeforeDelete.size() - 1));
        assertThat(contains, equalTo(false));
    }

    @Test
    public void testDeleteLikeWhenUsersLikeChanged() {
        int countLikes = likeCommentConfig.getMaxLikesModalView() + 5;
        Comment commentBeforeSave = comments.getFirst();
        commentBeforeSave.setLikes(0);
        Comment comment = commentRepository.save(commentBeforeSave);

        when(userClient.getUserExists(anyLong()))
                .thenReturn(true);

        for (int i = 0; i < countLikes; i++) {
            commentService.addLike(comment.getId().toHexString(), USER_ID + i);
        }

        List<LikeComment> likesCommentBeforeDelete = likeCommentRepository.findAll();
        Comment commentBeforeDeleteLike = commentRepository.findById(comment.getId()).orElse(null);
        commentService.deleteLike(comment.getId().toHexString(), USER_ID);
        Comment commentAfterDeleteLike = commentRepository.findById(comment.getId()).orElse(null);
        List<LikeComment> likesCommentAfterDelete = likeCommentRepository.findAll();
        boolean contains = likesCommentAfterDelete.stream()
                .anyMatch(likeComment -> likeComment.getCommentId().equals(comment.getId()) &&
                        likeComment.getAuthorId().equals(USER_ID));

        assertThat(commentBeforeDeleteLike, notNullValue());
        assertThat(commentAfterDeleteLike, notNullValue());
        assertThat(commentAfterDeleteLike.getLikes(), equalTo(commentBeforeDeleteLike.getLikes() - 1));
        assertThat(commentAfterDeleteLike.getLikedUsersId().getFirst(), equalTo(commentBeforeDeleteLike.getLikedUsersId().get(1)));
        assertThat(commentAfterDeleteLike.getLikedUsersId().getLast(), equalTo(commentBeforeDeleteLike.getLikedUsersId().getLast() + 1));
        assertThat(likesCommentBeforeDelete, notNullValue());
        assertThat(likesCommentAfterDelete, notNullValue());
        assertThat(likesCommentAfterDelete.size(), equalTo(likesCommentBeforeDelete.size() - 1));
        assertThat(contains, equalTo(false));
    }
}
