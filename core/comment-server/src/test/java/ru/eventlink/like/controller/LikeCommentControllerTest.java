package ru.eventlink.like.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.eventlink.comment.service.CommentPrivateService;
import ru.eventlink.dto.user.UserDto;
import ru.eventlink.like.service.LikeCommentService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LikeCommentController.class)
public class LikeCommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommentPrivateService commentPrivateService;

    @MockBean
    private LikeCommentService likeCommentService;

    private static final Long userId = 1L;

    private static final String commentId = "507f191e810c19729de860ea";

    private static List<UserDto> users;

    @BeforeAll
    public static void setUp() {
        users = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            UserDto user = UserDto.builder()
                    .id((long) i)
                    .email("user" + i + "@example.com")
                    .name("name" + i)
                    .rating(0)
                    .countFollowers(0)
                    .countFriends(0)
                    .build();
            users.add(user);
        }
    }

    @Test
    public void addValidLike() throws Exception {
        doNothing().when(commentPrivateService).addLike(anyString(), anyLong());

        mvc.perform(post("/api/v1/users/{userId}/events/comments/{commentId}/like", userId, commentId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());

        verify(commentPrivateService).addLike(anyString(), anyLong());
    }

    @Test
    public void addNotValidLike() throws Exception {
        doNothing().when(commentPrivateService).addLike(anyString(), anyLong());

        mvc.perform(post("/api/v1/users/{userId}/events/comments/{commentId}/like", (userId * -1), commentId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(containsString("Validation failure")));

        verify(commentPrivateService, never()).addLike(anyString(), anyLong());
    }

    @Test
    void testValidFindLikesByCommentId() throws Exception {
        when(likeCommentService.findLikesByCommentId(anyLong(), anyString(), anyInt()))
                .thenReturn(users);

        mvc.perform(get("/api/v1/users/{userId}/events/comments/{commentId}/like", userId, commentId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(users.size()))
                .andExpect(jsonPath("$[0].id").value(users.getFirst().getId()))
                .andExpect(jsonPath("$[0].email").value(users.getFirst().getEmail()))
                .andExpect(jsonPath("$[0].name").value(users.getFirst().getName()))
                .andExpect(jsonPath("$[0].rating").value(users.getFirst().getRating()));
    }

    @Test
    public void testNotValidFindLikesByCommentId() throws Exception {
        when(likeCommentService.findLikesByCommentId(anyLong(), anyString(), anyInt()))
                .thenReturn(users);

        mvc.perform(post("/api/v1/users/{userId}/events/comments/{commentId}/like", (userId * -1), commentId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(containsString("Validation failure")));
    }

    @Test
    public void deleteValidLike() throws Exception {
        doNothing().when(commentPrivateService).deleteLike(anyString(), anyLong());

        mvc.perform(delete("/api/v1/users/{userId}/events/comments/{commentId}/like", userId, commentId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());

        verify(commentPrivateService).deleteLike(anyString(), anyLong());
    }

    @Test
    public void deleteNotValidLike() throws Exception {
        doNothing().when(commentPrivateService).deleteLike(anyString(), anyLong());

        mvc.perform(post("/api/v1/users/{userId}/events/comments/{commentId}/like", (userId * -1), commentId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(containsString("Validation failure")));

        verify(commentPrivateService, never()).deleteLike(anyString(), anyLong());
    }
}
