package ru.eventlink.like.service;

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
import ru.eventlink.client.user.UserClient;
import ru.eventlink.dto.user.UserDto;
import ru.eventlink.exception.NotFoundException;
import ru.eventlink.exception.RestrictionsViolationException;
import ru.eventlink.like.model.LikeComment;
import ru.eventlink.like.repository.LikeCommentRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
public class LikeCommentServiceTest {

    @Autowired
    private LikeCommentRepository likeCommentRepository;

    @Autowired
    private LikeCommentService likeCommentService;

    @MockBean
    private UserClient userClient;

    private static final Long USER_ID = 1L;

    private static final ObjectId COMMENT_ID = new ObjectId("507f191e810c19729de860ea");

    private static List<LikeComment> likeComments;

    private static List<UserDto> users;

    @Container
    @ServiceConnection
    public static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:6.0"));

    @BeforeAll
    public static void setUp() {
        likeComments = new ArrayList<>();
        users = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            LikeComment likeComment = new LikeComment();
            likeComment.setAuthorId(USER_ID + i);
            likeComment.setCommentId(COMMENT_ID);
            likeComment.setCreationDate(LocalDateTime.now());

            UserDto userDto = UserDto.builder()
                    .id(USER_ID + i)
                    .email("email" + i + "@email.com")
                    .name("name " + i)
                    .rating(0L)
                    .build();

            likeComments.add(likeComment);
            users.add(userDto);
        }
    }

    @AfterEach
    public void cleanUp() {
        likeCommentRepository.deleteAll();
    }

    @Test
    public void testAddNotValidLike() {
        likeCommentRepository.save(likeComments.getFirst());

        assertThatExceptionOfType(RestrictionsViolationException.class)
                .isThrownBy(() -> likeCommentService.addLike(COMMENT_ID, USER_ID))
                .withMessage("User " + USER_ID + " have already rated comment " + COMMENT_ID);
    }

    @Test
    public void testDeleteNotValidLike() {
        assertThatExceptionOfType(RestrictionsViolationException.class)
                .isThrownBy(() -> likeCommentService.deleteLike(COMMENT_ID, USER_ID))
                .withMessage("User " + USER_ID + " did not rate comment " + COMMENT_ID);
    }

    @Test
    public void testFindLikesByCommentIdIfHaveLikes() {
        likeCommentRepository.saveAll(likeComments);

        when(userClient.getUserExists(anyLong()))
                .thenReturn(true);
        when(userClient.getAllUsers(anyList(), anyInt(), anyInt()))
                .thenReturn(users);

        List<UserDto> usersDto = likeCommentService.findLikesByCommentId(USER_ID, COMMENT_ID.toHexString(), 0);
        assertThat(usersDto, notNullValue());
        assertThat(usersDto.size(), equalTo(users.size()));
        assertThat(usersDto.getFirst().getId(), equalTo(users.getFirst().getId()));
    }

    @Test
    public void testFindLikesByCommentIdIfDontHaveLikes() {
        when(userClient.getUserExists(anyLong()))
                .thenReturn(true);
        when(userClient.getAllUsers(anyList(), anyInt(), anyInt()))
                .thenReturn(users);

        List<UserDto> usersDto = likeCommentService.findLikesByCommentId(USER_ID, COMMENT_ID.toHexString(), 0);
        assertThat(usersDto, notNullValue());
        assertThat(usersDto.size(), equalTo(0));
    }

    @Test
    public void testFindLikesByCommentIdIfUsersDontExist() {
        when(userClient.getUserExists(anyLong()))
                .thenReturn(false);

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> likeCommentService.findLikesByCommentId(USER_ID, COMMENT_ID.toHexString(), 0))
                .withMessage("User with id =" + USER_ID + " was not found. Only authorized users can view " +
                        "information about users who have liked it.");
    }
}
