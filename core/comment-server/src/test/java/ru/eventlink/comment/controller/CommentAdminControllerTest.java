package ru.eventlink.comment.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.eventlink.comment.service.CommentService;
import ru.eventlink.dto.comment.CommentUserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentAdminController.class)
public class CommentAdminControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommentService commentService;

    private static List<CommentUserDto> comments;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeAll
    static void setUp() {
        comments = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            CommentUserDto comment = CommentUserDto.builder()
                    .authorId(1L)
                    .text("text = " + i)
                    .creationDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .build();
            comments.add(comment);
        }
    }

    @Test
    void testValidFindCommentsByAuthorId() throws Exception {
        when(commentService.findAllCommentsByUserId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(comments);

        mvc.perform(get("/api/v1/admin/events/comments")
                .characterEncoding(StandardCharsets.UTF_8)
                .queryParam("userId", "1")
                .queryParam("sort", "DATE")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(comments.size()))
                .andExpect(jsonPath("$[0].authorId").value(comments.getFirst().getAuthorId()))
                .andExpect(jsonPath("$[0].text").value(comments.getFirst().getText()))
                .andExpect(jsonPath("$[0].creationDate").value(comments.getFirst().getCreationDate().format(formatter)))
                .andExpect(jsonPath("$[0].updateDate").value(comments.getFirst().getUpdateDate().format(formatter)));
    }

    @Test
    void testNotValidFindCommentsByAuthorId() throws Exception {
        when(commentService.findAllCommentsByUserId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(comments);

        mvc.perform(get("/api/v1/admin/events/comments")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .queryParam("userId", "-4")
                        .queryParam("sort", "DATE")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(containsString("Validation failure")));
    }
}
