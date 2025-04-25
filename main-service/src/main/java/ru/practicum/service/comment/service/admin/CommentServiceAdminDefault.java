package ru.practicum.service.comment.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.comment.repository.CommentRepository;
import ru.practicum.service.exception.CommentIsNotInRepositoryException;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceAdminDefault implements CommentServiceAdmin {

    private final CommentRepository commentRepository;

    @Override
    public void deleteById(Long commentId) {

        if (!commentRepository.existsById(commentId)) {
            throw new CommentIsNotInRepositoryException("Comment with id: " + commentId + " is not in repository");
        }

        commentRepository.deleteById(commentId);
    }
}
