package ru.practicum.service.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.comment.dto.CommentDto;
import ru.practicum.service.comment.dto.params.CommentPublicFilterParams;
import ru.practicum.service.comment.service.publics.CommentServicePublic;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentPublicController {

    private final CommentServicePublic commentServicePublic;

    @GetMapping("/{commentId}")
    public CommentDto findById(@Positive @PathVariable Long commentId) {
        log.trace("Getting comment by id: {} is started at controller-level", commentId);

        return commentServicePublic.findById(commentId);
    }

    @GetMapping
    public List<CommentDto> findAllEventCommentsByCriteria(
            @Valid @ModelAttribute CommentPublicFilterParams commentPublicFilterParams) {
        log.trace("Getting List of comments by criteria for event with id: {} is started at controller-level",
                commentPublicFilterParams.getEventId());

        return commentServicePublic.findAllEventCommentsByCriteria(commentPublicFilterParams);
    }
}
