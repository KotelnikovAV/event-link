package ru.eventlink.comment.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.eventlink.comment.service.CommentPublicService;
import ru.eventlink.dto.comment.CommentDto;

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

@WebMvcTest(controllers = CommentPublicController.class)
public class CommentPublicControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommentPublicService commentService;

    private static List<CommentDto> comments;

    private static final Long eventId = 1L;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeAll
    static void setUp() {
        comments = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            CommentDto commentDto = CommentDto.builder()
                    .id(i + "07f191e820c19729de860ea")
                    .eventId(eventId)
                    .authorId((long) i)
                    .text("This is a comment")
                    .countResponse(0)
                    .creationDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .deleted(false)
                    .build();
            comments.add(commentDto);
        }
    }

    @Test
    void testValidFindCommentsByAuthorId() throws Exception {
        when(commentService.findAllCommentsByEventId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(comments);

        mvc.perform(get("/api/v1/events/{eventId}/comments", eventId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .queryParam("sort", "DATE")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(comments.size()))
                .andExpect(jsonPath("$[0].id").value(comments.getFirst().getId()))
                .andExpect(jsonPath("$[0].text").value(comments.getFirst().getText()))
                .andExpect(jsonPath("$[0].authorId").value(comments.getFirst().getAuthorId()))
                .andExpect(jsonPath("$[0].eventId").value(comments.getFirst().getEventId()))
                .andExpect(jsonPath("$[0].countResponse").value(comments.getFirst().getCountResponse()))
                .andExpect(jsonPath("$[0].creationDate").value(comments.getFirst().getCreationDate().format(formatter)))
                .andExpect(jsonPath("$[0].updateDate").value(comments.getFirst().getUpdateDate().format(formatter)))
                .andExpect(jsonPath("$[0].deleted").value(comments.getFirst().getDeleted()));
    }

    @Test
    void testNotValidFindCommentsByAuthorId() throws Exception {
        when(commentService.findAllCommentsByEventId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(comments);

        mvc.perform(get("/api/v1/events/{eventId}/comments", eventId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .queryParam("sort", "DATE")
                        .queryParam("page", "-5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(containsString("Validation failure")));
    }
}
