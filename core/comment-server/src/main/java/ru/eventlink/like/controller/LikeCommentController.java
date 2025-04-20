package ru.eventlink.like.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.eventlink.comment.service.CommentPrivateService;
import ru.eventlink.dto.user.UserDto;
import ru.eventlink.like.service.LikeCommentService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{userId}/events/comments/{commentId}/like")
@RequiredArgsConstructor
@Slf4j
public class LikeCommentController {
    private final CommentPrivateService commentPrivateService;
    private final LikeCommentService likeCommentService;

    @PostMapping
    public void addLike(@PathVariable @Positive Long userId,
                        @PathVariable String commentId) {
        log.info("Add like comment = {} by userId = {}", commentId, userId);
        commentPrivateService.addLike(commentId, userId);
    }

    @GetMapping
    public List<UserDto> findLikesByCommentId(@PathVariable @Positive Long userId,
                                              @PathVariable String commentId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero int page) {
        log.info("Get likes by userId = {}, comment = {}", userId, commentId);
        return likeCommentService.findLikesByCommentId(userId, commentId, page);
    }

    @DeleteMapping
    public void deleteLike(@PathVariable @Positive Long userId,
                           @PathVariable String commentId) {
        log.info("Delete like comment = {} by userId = {}", commentId, userId);
        commentPrivateService.deleteLike(commentId, userId);
    }
}
