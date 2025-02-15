package ru.eventlink.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.eventlink.comment.model.Comment;
import ru.eventlink.dto.comment.CommentDto;
import ru.eventlink.dto.comment.RequestCommentDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {
    @Mapping(target = "parentCommentId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    Comment requestCommentDtoToComment(RequestCommentDto requestCommentDto);

    @Mapping(target = "usersLiked", ignore = true)
    CommentDto commentToCommentDto(Comment comment);
}
