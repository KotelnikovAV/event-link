package ru.eventlink.comment.service;

import ru.eventlink.dto.comment.CommentDto;
import ru.eventlink.enums.CommentSort;

import java.util.List;

public interface CommentPublicService {
    List<CommentDto> findAllCommentsByEventId(Long eventId, CommentSort commentSort, int page, int size);
}
