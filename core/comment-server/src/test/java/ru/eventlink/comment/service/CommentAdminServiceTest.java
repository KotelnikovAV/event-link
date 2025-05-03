package ru.eventlink.comment.service;

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
import ru.eventlink.client.user.UserAdminClient;
import ru.eventlink.comment.model.Comment;
import ru.eventlink.comment.repository.CommentRepository;
import ru.eventlink.dto.comment.CommentUserDto;
import ru.eventlink.enums.CommentSort;
import ru.eventlink.like.repository.LikeCommentRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
public class CommentAdminServiceTest {

    @Autowired
    private CommentAdminService commentAdminService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeCommentRepository likeCommentRepository;

    @MockBean
    private UserAdminClient userAdminClient;

    private static final Long USER_ID = 1L;

    private static final Long EVENT_ID = 1L;

    private static List<Comment> comments;

    @Container
    @ServiceConnection
    public static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:6.0"));

    @BeforeAll
    public static void setUp() throws InterruptedException {
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
    public void testFindAllCommentsByUserIdAndSortDate() {
        commentRepository.saveAll(comments);

        when(userAdminClient.getUserExists(anyLong()))
                .thenReturn(true);

        List<CommentUserDto> commentsUserDto = commentAdminService.findAllCommentsByUserId(USER_ID, CommentSort.DATE, 0, 10);
        assertThat(commentsUserDto, notNullValue());
        assertThat(commentsUserDto.size(), equalTo(1));
        assertThat(commentsUserDto.getFirst().getAuthorId(), equalTo(USER_ID));
    }
}
