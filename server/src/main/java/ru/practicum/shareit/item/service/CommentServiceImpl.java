package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.GlobalProperties.DATE_ZONE_ID;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional(readOnly = true)
    public Comment getComment(Long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        return optionalComment.orElseThrow(() -> new NotFoundException(String.format("Комментарий с id=%d не найдена", id)));
    }

    @Override
    @Transactional
    public Comment createComment(Comment comment) {
        if (!bookingRepository.existsByItemAndBookerAndStatusNotAndStartLessThanEqual(comment.getItem(),
                comment.getAuthor(),
                LocalDateTime.now(DATE_ZONE_ID))) {
            throw new ValidationException(String.format("Нельзя добавить комментарий к вещи %d " +
                            "у которой пользователь %d не оставлял бронирований или бронирование еще не начато",
                    comment.getItem().getId(), comment.getAuthor().getId()));
        }
        return commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getItemComments(Item item) {
        return commentRepository.findByItemOrderByIdAsc(item);
    }
}
