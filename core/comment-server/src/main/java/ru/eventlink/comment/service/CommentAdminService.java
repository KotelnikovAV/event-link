package ru.eventlink.comment.service;

import ru.eventlink.dto.comment.CommentUserDto;
import ru.eventlink.enums.CommentSort;

import java.util.List;

public interface CommentAdminService {

    List<CommentUserDto> findAllCommentsByUserId(Long userId, CommentSort commentSort, int page, int size);
}
