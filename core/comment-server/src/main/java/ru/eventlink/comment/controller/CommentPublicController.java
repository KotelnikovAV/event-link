package ru.eventlink.comment.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.eventlink.comment.service.CommentService;
import ru.eventlink.dto.comment.CommentDto;
import ru.eventlink.enums.CommentSort;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events/{eventId}/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentPublicController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> findAllCommentsByEventId(@PathVariable @Positive Long eventId,
                                                     @RequestParam(required = false) CommentSort commentSort,
                                                     @RequestParam(defaultValue = "0") @PositiveOrZero int page,
                                                     @RequestParam(defaultValue = "15") @Positive int size) {
        log.info("Get all comments event by eventId = {}, sort = {}", eventId, commentSort);
        return commentService.findAllCommentsByEventId(eventId, commentSort, page, size);
    }
}
