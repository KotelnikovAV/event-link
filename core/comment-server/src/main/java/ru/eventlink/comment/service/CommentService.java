package ru.eventlink.comment.service;

import ru.eventlink.dto.comment.CommentDto;
import ru.eventlink.dto.comment.CommentUserDto;
import ru.eventlink.dto.comment.RequestCommentDto;

import java.util.List;

public interface CommentService {
    List<CommentDto> findAllCommentsByEventId(Long eventId);

    List<CommentUserDto> findAllCommentsByUserId(Long userId);

    CommentDto addComment(Long eventId, RequestCommentDto commentDto);

    CommentDto updateComment(RequestCommentDto commentDto);

    CommentDto addSubComment(String parentCommentId, RequestCommentDto commentDto);
}
