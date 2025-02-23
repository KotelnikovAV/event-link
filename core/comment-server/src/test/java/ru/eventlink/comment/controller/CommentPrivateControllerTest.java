package ru.eventlink.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.eventlink.comment.service.CommentService;
import ru.eventlink.dto.comment.CommentDto;
import ru.eventlink.dto.comment.RequestCommentDto;
import ru.eventlink.dto.comment.UpdateCommentDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentPrivateController.class)
public class CommentPrivateControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommentService commentService;

    private static RequestCommentDto validRequestCommentDto;

    private static RequestCommentDto notValidRequestCommentDto;

    private static UpdateCommentDto validUpdateCommentDto;

    private static UpdateCommentDto notValidUpdateCommentDto;

    private static CommentDto commentDto;

    private static final Long userId = 1L;

    private static final Long eventId = 1L;

    private static final String commentId = "507f191e810c19729de860ea";

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeAll
    public static void setUp() {
        validRequestCommentDto = RequestCommentDto.builder()
                .eventId(eventId)
                .authorId(userId)
                .text("This is a comment")
                .build();

        notValidRequestCommentDto = RequestCommentDto.builder()
                .authorId(userId)
                .text("This is a comment")
                .build();

        validUpdateCommentDto = UpdateCommentDto.builder()
                .text("This is a comment")
                .build();

        notValidUpdateCommentDto = UpdateCommentDto.builder()
                .build();

        commentDto = CommentDto.builder()
                .id("507f191e820c19729de860ea")
                .eventId(eventId)
                .authorId(userId)
                .text("This is a comment")
                .countResponse(0)
                .creationDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .deleted(false)
                .build();
    }

    @Test
    void testAddComments() throws Exception {
        when(commentService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/api/v1/users/{userId}/events/{eventId}/comments", userId, eventId)
                        .content(objectMapper.writeValueAsString(validRequestCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorId").value(commentDto.getAuthorId()))
                .andExpect(jsonPath("$.eventId").value(commentDto.getEventId()))
                .andExpect(jsonPath("$.countResponse").value(commentDto.getCountResponse()))
                .andExpect(jsonPath("$.creationDate").value(commentDto.getCreationDate().format(formatter)))
                .andExpect(jsonPath("$.updateDate").value(commentDto.getUpdateDate().format(formatter)))
                .andExpect(jsonPath("$.deleted").value(commentDto.getDeleted()));
    }

    @Test
    void testAddNotValidComments() throws Exception {
        when(commentService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/api/v1/users/{userId}/events/{eventId}/comments", userId, eventId)
                        .content(objectMapper.writeValueAsString(notValidRequestCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(containsString("Validation failure")));
    }

    @Test
    void testUpdateComments() throws Exception {
        when(commentService.updateComment(anyLong(), anyString(), any()))
                .thenReturn(commentDto);

        mvc.perform(patch("/api/v1/users/{userId}/events/comments/{commentId}", userId, commentId)
                        .content(objectMapper.writeValueAsString(validUpdateCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorId").value(commentDto.getAuthorId()))
                .andExpect(jsonPath("$.eventId").value(commentDto.getEventId()))
                .andExpect(jsonPath("$.countResponse").value(commentDto.getCountResponse()))
                .andExpect(jsonPath("$.creationDate").value(commentDto.getCreationDate().format(formatter)))
                .andExpect(jsonPath("$.updateDate").value(commentDto.getUpdateDate().format(formatter)))
                .andExpect(jsonPath("$.deleted").value(commentDto.getDeleted()));
    }

    @Test
    void testUpdateNotValidComments() throws Exception {
        when(commentService.updateComment(anyLong(), anyString(), any()))
                .thenReturn(commentDto);

        mvc.perform(patch("/api/v1/users/{userId}/events/comments/{commentId}", userId, commentId)
                        .content(objectMapper.writeValueAsString(notValidUpdateCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(containsString("Validation failure")));
    }

    @Test
    void testAddSubComments() throws Exception {
        when(commentService.addSubComment(anyLong(), anyString(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/api/v1/users/{userId}/events/comments/{commentId}", userId, commentId)
                        .content(objectMapper.writeValueAsString(validRequestCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorId").value(commentDto.getAuthorId()))
                .andExpect(jsonPath("$.eventId").value(commentDto.getEventId()))
                .andExpect(jsonPath("$.countResponse").value(commentDto.getCountResponse()))
                .andExpect(jsonPath("$.creationDate").value(commentDto.getCreationDate().format(formatter)))
                .andExpect(jsonPath("$.updateDate").value(commentDto.getUpdateDate().format(formatter)))
                .andExpect(jsonPath("$.deleted").value(commentDto.getDeleted()));
    }

    @Test
    void testAddNotValidSubComments() throws Exception {
        when(commentService.addSubComment(anyLong(), anyString(), any()))
                .thenReturn(commentDto);

        mvc.perform(patch("/api/v1/users/{userId}/events/comments/{commentId}", userId, commentId)
                        .content(objectMapper.writeValueAsString(notValidUpdateCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(containsString("Validation failure")));
    }

    @Test
    void testDeleteComments() throws Exception {
        when(commentService.deleteComment(anyLong(), anyString()))
                .thenReturn(commentDto);

        mvc.perform(delete("/api/v1/users/{userId}/events/comments/{commentId}", userId, commentId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorId").value(commentDto.getAuthorId()))
                .andExpect(jsonPath("$.eventId").value(commentDto.getEventId()))
                .andExpect(jsonPath("$.countResponse").value(commentDto.getCountResponse()))
                .andExpect(jsonPath("$.creationDate").value(commentDto.getCreationDate().format(formatter)))
                .andExpect(jsonPath("$.updateDate").value(commentDto.getUpdateDate().format(formatter)))
                .andExpect(jsonPath("$.deleted").value(commentDto.getDeleted()));
    }

    @Test
    void testDeleteNotValidSubComments() throws Exception {
        when(commentService.deleteComment(anyLong(), anyString()))
                .thenReturn(commentDto);

        mvc.perform(patch("/api/v1/users/{userId}/events/comments/{commentId}", (userId * -1), commentId)
                        .content(objectMapper.writeValueAsString(notValidUpdateCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(containsString("Validation failure")));
    }
}
