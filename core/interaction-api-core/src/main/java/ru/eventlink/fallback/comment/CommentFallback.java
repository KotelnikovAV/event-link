package ru.eventlink.fallback.comment;

import ru.eventlink.client.comment.CommentClient;
import ru.eventlink.dto.comment.CommentDto;
import ru.eventlink.dto.comment.CommentUserDto;
import ru.eventlink.dto.comment.RequestCommentDto;
import ru.eventlink.exception.ServerUnavailableException;

import java.util.List;

public class CommentFallback implements CommentClient {
    @Override
    public List<CommentDto> findAllCommentsByEventId(Long eventId) {
        throw new ServerUnavailableException("Endpoint /api/v1/admin/events/{eventId}/comments method GET is unavailable");
    }

    @Override
    public List<CommentUserDto> findAllCommentsByUserId(Long userId) {
        throw new ServerUnavailableException("Endpoint /api/v1/admin/events/comments method GET is unavailable");
    }

    @Override
    public CommentDto addComment(Long eventId, RequestCommentDto commentDto) {
        throw new ServerUnavailableException("Endpoint /api/v1/events/{eventId}/comments method POST is unavailable");
    }

    @Override
    public CommentDto updateComment(String commentId, RequestCommentDto commentDto) {
        throw new ServerUnavailableException("Endpoint /api/v1/events/{eventId}/comments method PUT is unavailable");
    }

    @Override
    public CommentDto addSubComment(String parentCommentId, RequestCommentDto commentDto) {
        throw new ServerUnavailableException("Endpoint /api/v1/events/{eventId}/comments/{commentId} method POST is unavailable");
    }
}
