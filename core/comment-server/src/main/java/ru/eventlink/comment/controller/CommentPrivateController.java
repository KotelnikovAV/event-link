package ru.eventlink.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.eventlink.comment.service.CommentService;
import ru.eventlink.dto.comment.CommentDto;
import ru.eventlink.dto.comment.RequestCommentDto;
import ru.eventlink.dto.comment.UpdateCommentDto;

@RestController
@RequestMapping("/api/v1/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping("/{eventId}/comments")
    public CommentDto addComment(@PathVariable @Positive Long userId,
                                 @PathVariable @Positive Long eventId,
                                 @RequestBody @Valid RequestCommentDto commentDto) {
        log.info("Received a POST request to add comment {} from userId = {} for eventId = {}", commentDto, userId, eventId);
        return commentService.addComment(userId, eventId, commentDto);
    }

    @PatchMapping("/comments/{commentId}")
    public CommentDto updateComment(@PathVariable @Positive Long userId,
                                    @PathVariable String commentId,
                                    @RequestBody @Valid UpdateCommentDto updateCommentDto) {
        log.info("Received a PATCH request to update comment with commentId = {} with body = {} from userId = {}",
                commentId, updateCommentDto, userId);
        return commentService.updateComment(userId, commentId, updateCommentDto);
    }

    @PostMapping("/comments/{commentId}")
    public CommentDto addSubComment(@PathVariable @Positive Long userId,
                                    @PathVariable(name = "commentId") String parentCommentId,
                                    @RequestBody @Valid RequestCommentDto commentDto) {
        log.info("Received a POST request to add response = {} to commentId {} from userId = {}",
                commentDto, parentCommentId, userId);
        return commentService.addSubComment(userId, parentCommentId, commentDto);
    }

    @DeleteMapping("/comments/{commentId}")
    public CommentDto deleteComment(@PathVariable @Positive Long userId,
                                    @PathVariable String commentId) {
        log.info("Received a DELETE request to delete comment with commentId = {}", commentId);
        return commentService.deleteComment(userId, commentId);
    }
}
