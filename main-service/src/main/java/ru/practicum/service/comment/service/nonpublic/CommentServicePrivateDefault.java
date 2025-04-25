package ru.practicum.service.comment.service.nonpublic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.comment.dto.CommentDto;
import ru.practicum.service.comment.dto.CommentRequestDto;
import ru.practicum.service.comment.dto.mapper.CommentMapper;
import ru.practicum.service.comment.dto.params.CommentPrivateFilterParams;
import ru.practicum.service.comment.model.Comment;
import ru.practicum.service.comment.repository.CommentRepository;
import ru.practicum.service.comment.service.SupportService;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.repository.EventRepository;
import ru.practicum.service.exception.CommentIsNotInRepositoryException;
import ru.practicum.service.user.model.User;
import ru.practicum.service.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.service.comment.dto.mapper.CommentMapper.toComment;
import static ru.practicum.service.comment.dto.mapper.CommentMapper.toCommentDto;

@Service
@Transactional(readOnly = true)
public class CommentServicePrivateDefault extends SupportService implements CommentServicePrivate {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentServicePrivateDefault(UserRepository userRepository,
                                        EventRepository eventRepository,
                                        CommentRepository commentRepository) {
        super(userRepository, eventRepository);
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public CommentDto add(Long userId, Long eventId, CommentRequestDto commentRequestDto) {

        User user = findUserByIdIfExists(userId);
        Event event = findEventByIdIfExists(eventId);

        Comment comment = toComment(commentRequestDto, user, event);

        return toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> findAllUserCommentsByCriteria(
            Long userId,
            CommentPrivateFilterParams commentPrivateFilterParams) {

        findUserByIdIfExists(userId);

        LocalDateTime rangeStart = commentPrivateFilterParams.getRangeStart();
        LocalDateTime rangeEnd = commentPrivateFilterParams.getRangeEnd();

        validateTimeFilters(rangeStart, rangeEnd);

        Pageable pageable = getPageable(commentPrivateFilterParams.getFrom(), commentPrivateFilterParams.getSize(),
                commentPrivateFilterParams.getSort());

        return commentRepository.getAllUserComments(userId, rangeStart, rangeEnd, pageable)
                .stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto updateById(Long userId, Long commentId, CommentRequestDto commentRequestDto) {

        findUserByIdIfExists(userId);

        Comment commentFromRepository = findCommentByIdAndAuthorIdIfExists(commentId, userId);

        commentFromRepository.setText(commentRequestDto.getText());
        commentFromRepository.setUpdatedOn(LocalDateTime.now());

        return toCommentDto(commentRepository.save(commentFromRepository));
    }

    @Override
    @Transactional
    public void deleteById(Long userId, Long commentId) {
        findUserByIdIfExists(userId);

        findCommentByIdAndAuthorIdIfExists(commentId, userId);

        commentRepository.deleteById(commentId);
    }

    private Comment findCommentByIdAndAuthorIdIfExists(Long commentId, Long userId) {
        return commentRepository.findByIdAndAuthorId(commentId, userId).orElseThrow(() ->
                new CommentIsNotInRepositoryException("There's no comment with commentId: " + commentId +
                        " by user with id: " + userId));
    }
}
