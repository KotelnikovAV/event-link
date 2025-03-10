package ru.eventlink.client.comment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.eventlink.dto.comment.CommentDto;
import ru.eventlink.dto.comment.CommentUserDto;
import ru.eventlink.dto.comment.RequestCommentDto;
import ru.eventlink.dto.comment.UpdateCommentDto;
import ru.eventlink.dto.user.UserDto;
import ru.eventlink.enums.CommentSort;
import ru.eventlink.fallback.comment.CommentFallback;

import java.util.List;

@FeignClient(name = "comment-server", fallback = CommentFallback.class)
public interface CommentClient {
    @GetMapping("/api/v1/events/{eventId}/comments")
    List<CommentDto> findAllCommentsByEventId(@PathVariable @Positive Long eventId,
                                              @RequestParam(required = false) CommentSort commentSort,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero int page,
                                              @RequestParam(defaultValue = "15") @Positive int size);

    @GetMapping("/api/v1/admin/events/comments")
    List<CommentUserDto> findAllCommentsByUserId(@RequestParam @Positive Long userId,
                                                 @RequestParam(required = false) CommentSort commentSort,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero int page,
                                                 @RequestParam(defaultValue = "50") @Positive int size);

    @PostMapping("/api/v1/users/{userId}/events/{eventId}/comments")
    CommentDto addComment(@PathVariable @Positive Long userId,
                          @PathVariable @Positive Long eventId,
                          @RequestBody @Valid RequestCommentDto commentDto);

    @PatchMapping("/api/v1/users/{userId}/events/comments/{commentId}")
    CommentDto updateComment(@PathVariable @Positive Long userId,
                             @PathVariable String commentId,
                             @RequestBody @Valid UpdateCommentDto updateCommentDto);

    @PostMapping("/api/v1/users/{userId}/events/comments/{commentId}")
    CommentDto addSubComment(@PathVariable @Positive Long userId,
                             @PathVariable(name = "commentId") String parentCommentId,
                             @RequestBody @Valid RequestCommentDto commentDto);

    @DeleteMapping("/api/v1/users/{userId}/events/comments/{commentId}")
    void deleteComment(@PathVariable @Positive Long userId,
                       @PathVariable String commentId);

    @PostMapping("/api/v1/users/{userId}/events/comments/{commentId}/like")
    void addLike(@PathVariable @Positive Long userId,
                 @PathVariable String commentId);

    @GetMapping("/api/v1/users/{userId}/events/comments/{commentId}/like")
    List<UserDto> findLikesByCommentId(@PathVariable @Positive Long userId,
                                       @PathVariable String commentId,
                                       @RequestParam(defaultValue = "0") @PositiveOrZero int page);

    @DeleteMapping("/api/v1/users/{userId}/events/comments/{commentId}/like")
    void deleteLike(@PathVariable @Positive Long userId,
                    @PathVariable String commentId);
}
