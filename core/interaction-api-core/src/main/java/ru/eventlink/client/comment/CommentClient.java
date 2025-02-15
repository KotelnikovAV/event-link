package ru.eventlink.client.comment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.eventlink.dto.comment.CommentDto;
import ru.eventlink.dto.comment.CommentUserDto;
import ru.eventlink.dto.comment.RequestCommentDto;
import ru.eventlink.fallback.comment.CommentFallback;

import java.util.List;

@FeignClient(name = "comment-server", fallback = CommentFallback.class)
public interface CommentClient {
    @GetMapping("/api/v1/admin/events/{eventId}/comments")
    List<CommentDto> findAllCommentsByEventId(@PathVariable @Positive Long eventId);

    @GetMapping("/api/v1/admin/events/comments")
    List<CommentUserDto> findAllCommentsByUserId(@RequestParam @Positive Long userId);

    @PostMapping("/api/v1/events/{eventId}/comments")
    CommentDto addComment(@PathVariable @Positive Long eventId, @RequestBody @Valid RequestCommentDto commentDto);

    @PutMapping("/api/v1/events/{eventId}/comments/{commentId}")
    CommentDto updateComment(@PathVariable String commentId, @RequestBody @Valid RequestCommentDto commentDto);

    @PostMapping("/api/v1/events/{eventId}/comments/{commentId}")
    CommentDto addSubComment(@PathVariable(name = "commentId") String parentCommentId,
                             @RequestBody @Valid RequestCommentDto commentDto);
}
