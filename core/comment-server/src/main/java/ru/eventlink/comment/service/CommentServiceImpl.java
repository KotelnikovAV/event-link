package ru.eventlink.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.eventlink.comment.mapper.CommentMapper;
import ru.eventlink.comment.repository.CommentRepository;
import ru.eventlink.dto.comment.CommentDto;
import ru.eventlink.dto.comment.CommentUserDto;
import ru.eventlink.dto.comment.RequestCommentDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public List<CommentDto> findAllCommentsByEventId(Long eventId) {
        return List.of();
    }

    @Override
    public List<CommentUserDto> findAllCommentsByUserId(Long userId) {
        return List.of();
    }

    @Override
    public CommentDto addComment(Long eventId, RequestCommentDto commentDto) {
        return null;
    }

    @Override
    public CommentDto updateComment(RequestCommentDto commentDto) {
        return null;
    }

    @Override
    public CommentDto addSubComment(String parentCommentId, RequestCommentDto commentDto) {
        return null;
    }
}
