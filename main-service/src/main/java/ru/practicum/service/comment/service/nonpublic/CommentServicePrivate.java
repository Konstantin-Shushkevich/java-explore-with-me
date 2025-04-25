package ru.practicum.service.comment.service.nonpublic;

import ru.practicum.service.comment.dto.CommentDto;
import ru.practicum.service.comment.dto.CommentRequestDto;
import ru.practicum.service.comment.dto.params.CommentPrivateFilterParams;

import java.util.List;

public interface CommentServicePrivate {

    CommentDto add(Long userId, Long eventId, CommentRequestDto commentRequestDto);

    List<CommentDto> findAllUserCommentsByCriteria(Long userId, CommentPrivateFilterParams commentPrivateFilterParams);

    CommentDto updateById(Long userId, Long commentId, CommentRequestDto commentRequestDto);

    void deleteById(Long userId, Long commentId);


}
