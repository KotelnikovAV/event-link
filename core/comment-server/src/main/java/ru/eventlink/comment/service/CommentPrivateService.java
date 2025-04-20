package ru.eventlink.comment.service;

import ru.eventlink.dto.comment.CommentDto;
import ru.eventlink.dto.comment.RequestCommentDto;
import ru.eventlink.dto.comment.UpdateCommentDto;

public interface CommentPrivateService {

    CommentDto addComment(Long userId, Long eventId, RequestCommentDto commentDto);

    CommentDto updateComment(Long userId, String commentId, UpdateCommentDto updateCommentDto);

    CommentDto addSubComment(Long userId, String parentCommentId, RequestCommentDto commentDto);

    CommentDto deleteComment(Long userId, String commentId);

    void addLike(String commentId, Long authorId);

    void deleteLike(String commentId, Long authorId);
}
