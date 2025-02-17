package ru.eventlink.comment.service;

import ru.eventlink.dto.comment.CommentDto;
import ru.eventlink.dto.comment.CommentUserDto;
import ru.eventlink.dto.comment.RequestCommentDto;
import ru.eventlink.dto.comment.UpdateCommentDto;
import ru.eventlink.enums.CommentSort;

import java.util.List;

public interface CommentService {
    List<CommentDto> findAllCommentsByEventId(Long eventId, CommentSort commentSort, int page, int size);

    List<CommentUserDto> findAllCommentsByUserId(Long userId, CommentSort commentSort, int page, int size);

    CommentDto addComment(Long userId, Long eventId, RequestCommentDto commentDto);

    CommentDto updateComment(Long userId, Long eventId, String commentId, UpdateCommentDto updateCommentDto);

    CommentDto addSubComment(Long userId, String parentCommentId, RequestCommentDto commentDto);

    CommentDto deleteComment(Long userId, String commentId);
}
