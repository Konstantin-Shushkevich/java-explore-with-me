package ru.practicum.service.comment.service.publics;

import ru.practicum.service.comment.dto.CommentDto;
import ru.practicum.service.comment.dto.params.CommentPublicFilterParams;

import java.util.List;

public interface CommentServicePublic {

    CommentDto findById(Long commentId);

    List<CommentDto> findAllEventCommentsByCriteria(CommentPublicFilterParams commentPublicFilterParams);
}
