package ru.eventlink.fallback.comment;

import ru.eventlink.client.comment.CommentClient;
import ru.eventlink.dto.comment.CommentDto;
import ru.eventlink.dto.comment.CommentUserDto;
import ru.eventlink.dto.comment.RequestCommentDto;
import ru.eventlink.dto.comment.UpdateCommentDto;
import ru.eventlink.dto.user.UserDto;
import ru.eventlink.enums.CommentSort;
import ru.eventlink.exception.ServerUnavailableException;

import java.util.List;

public class CommentFallback implements CommentClient {
    @Override
    public List<CommentDto> findAllCommentsByEventId(Long eventId, CommentSort commentSort, int page, int size) {
        throw new ServerUnavailableException("Endpoint /api/v1/admin/events/{eventId}/comments method GET is unavailable");
    }

    @Override
    public List<CommentUserDto> findAllCommentsByUserId(Long userId, CommentSort commentSort, int page, int size) {
        throw new ServerUnavailableException("Endpoint /api/v1/admin/events/comments method GET is unavailable");
    }

    @Override
    public CommentDto addComment(Long userId, Long eventId, RequestCommentDto commentDto) {
        throw new ServerUnavailableException("Endpoint /api/v1/users/{userId}/events/{eventId}/comments method POST " +
                "is unavailable");
    }

    @Override
    public CommentDto updateComment(Long userId, String commentId, UpdateCommentDto updateCommentDto) {
        throw new ServerUnavailableException("Endpoint /api/v1/users/{userId}/events/{eventId}/comments/{commentId} " +
                "method PATCH is unavailable");
    }

    @Override
    public CommentDto addSubComment(Long userId, String parentCommentId, RequestCommentDto commentDto) {
        throw new ServerUnavailableException("Endpoint /api/v1/users/{userId}/events/{eventId}/comments/{commentId} " +
                "method POST is unavailable");
    }

    @Override
    public void deleteComment(Long userId, String commentId) {
        throw new ServerUnavailableException("Endpoint /api/v1/users/{userId}/events/{eventId}/comments/{commentId} " +
                "method DELETE is unavailable");
    }

    @Override
    public void addLike(Long userId, String commentId) {
        throw new ServerUnavailableException("Endpoint /api/v1/users/{userId}/events/comments/{commentId}/like" +
                " method POST is unavailable");
    }

    @Override
    public List<UserDto> findLikesByCommentId(Long userId, String commentId, int page) {
        throw new ServerUnavailableException("Endpoint /api/v1/users/{userId}/events/comments/{commentId}/like" +
                " method GET is unavailable");
    }

    @Override
    public void deleteLike(Long userId, String commentId) {
        throw new ServerUnavailableException("Endpoint /api/v1/users/{userId}/events/comments/{commentId}/like" +
                " method PATCH is unavailable");
    }
}
