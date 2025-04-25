package ru.practicum.service.comment.service.publics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.service.comment.dto.CommentDto;
import ru.practicum.service.comment.dto.mapper.CommentMapper;
import ru.practicum.service.comment.dto.params.CommentPublicFilterParams;
import ru.practicum.service.comment.model.Comment;
import ru.practicum.service.comment.repository.CommentRepository;
import ru.practicum.service.comment.service.SupportService;
import ru.practicum.service.event.repository.EventRepository;
import ru.practicum.service.exception.CommentIsNotInRepositoryException;
import ru.practicum.service.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.service.comment.dto.mapper.CommentMapper.toCommentDto;

@Service
public class CommentServicePublicDefault extends SupportService implements CommentServicePublic {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentServicePublicDefault(UserRepository userRepository,
                                       EventRepository eventRepository,
                                       CommentRepository commentRepository) {
        super(userRepository, eventRepository);
        this.commentRepository = commentRepository;
    }

    @Override
    public CommentDto findById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentIsNotInRepositoryException("Comment with id: " + commentId +
                        " is not in repository"));

        return toCommentDto(comment);
    }

    @Override
    public List<CommentDto> findAllEventCommentsByCriteria(CommentPublicFilterParams commentPublicFilterParams) {

        Long eventId = commentPublicFilterParams.getEventId();
        findEventByIdIfExists(eventId);


        LocalDateTime rangeStart = commentPublicFilterParams.getRangeStart();
        LocalDateTime rangeEnd = commentPublicFilterParams.getRangeEnd();
        validateTimeFilters(rangeStart, rangeEnd);

        boolean participantsOnly = commentPublicFilterParams.isParticipantsOnly();
        Pageable pageable = getPageable(commentPublicFilterParams.getFrom(),
                commentPublicFilterParams.getSize(), commentPublicFilterParams.getSort());

        return commentRepository.getCommentsByEventId(eventId, rangeStart, rangeEnd, participantsOnly, pageable)
                .stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }
}
