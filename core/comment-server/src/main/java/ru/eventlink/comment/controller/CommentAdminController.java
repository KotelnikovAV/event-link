package ru.eventlink.comment.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.eventlink.comment.service.CommentAdminService;
import ru.eventlink.dto.comment.CommentUserDto;
import ru.eventlink.enums.CommentSort;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/events/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentAdminController {
    private final CommentAdminService commentAdminService;

    @GetMapping
    public List<CommentUserDto> findAllCommentsByUserId(@RequestParam @Positive Long userId,
                                                        @RequestParam(required = false) CommentSort sort,
                                                        @RequestParam(defaultValue = "0") @PositiveOrZero int page,
                                                        @RequestParam(defaultValue = "50") @Positive int size) {
        log.info("Get all comments event by userId = {}, sort = {}", userId, sort);
        return commentAdminService.findAllCommentsByUserId(userId, sort, page, size);
    }
}
