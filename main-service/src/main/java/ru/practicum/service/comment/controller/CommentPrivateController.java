package ru.practicum.service.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.comment.dto.CommentDto;
import ru.practicum.service.comment.dto.CommentRequestDto;
import ru.practicum.service.comment.dto.params.CommentPrivateFilterParams;
import ru.practicum.service.comment.service.nonpublic.CommentServicePrivate;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
public class CommentPrivateController {

    private final CommentServicePrivate commentServicePrivate;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto add(@Positive @PathVariable Long userId,
                          @Positive @RequestParam Long eventId,
                          @Valid @RequestBody CommentRequestDto commentRequestDto) {
        log.trace("Adding comment by user with id: {} for event with id: {} is started at controller-level",
                userId,
                eventId);

        return commentServicePrivate.add(userId, eventId, commentRequestDto);
    }

    @GetMapping
    public List<CommentDto> findAllUserCommentsByCriteria(
            @Positive @PathVariable Long userId,
            @Valid @ModelAttribute CommentPrivateFilterParams commentPrivateFilterParams) {
        log.trace("Getting List of comments (user with id: {}) by criteria is started at controller-level", userId);

        return commentServicePrivate.findAllUserCommentsByCriteria(userId, commentPrivateFilterParams);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateById(@Positive @PathVariable Long userId,
                                 @Positive @PathVariable Long commentId,
                                 @Valid @RequestBody CommentRequestDto commentRequestDto) {
        log.trace("Updating of comment with id: {} by user with id: {} is started at controller-level",
                commentId,
                userId);

        return commentServicePrivate.updateById(userId, commentId, commentRequestDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Positive @PathVariable Long userId,
                           @Positive @PathVariable Long commentId) {
        log.trace("Deletion of comment with id: {} by user with id: {} is started at controller-level",
                commentId,
                userId);

        commentServicePrivate.deleteById(userId, commentId);
    }
}
