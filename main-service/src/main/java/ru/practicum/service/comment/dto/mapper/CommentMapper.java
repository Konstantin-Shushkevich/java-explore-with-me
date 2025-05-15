package ru.practicum.service.comment.dto.mapper;

import ru.practicum.service.comment.dto.CommentDto;
import ru.practicum.service.comment.dto.CommentRequestDto;
import ru.practicum.service.comment.model.Comment;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.user.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

import static ru.practicum.service.user.dto.mapper.UserMapper.toUserShortDto;

public class CommentMapper {
    public static Comment toComment(CommentRequestDto commentRequestDto, User user, Event event) {
        return Comment.builder()
                .text(commentRequestDto.getText())
                .author(user)
                .event(event)
                .createdOn(LocalDateTime.now())
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .author(toUserShortDto(comment.getAuthor()))
                .eventId(comment.getEvent().getId())
                .createdOn(comment.getCreatedOn())
                .updatedOn(Objects.isNull(comment.getUpdatedOn()) ? null : comment.getUpdatedOn())
                .build();
    }
}
