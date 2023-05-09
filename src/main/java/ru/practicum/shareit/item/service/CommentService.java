package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface CommentService {
    Comment getComment(Long id);

    Comment createComment(Comment comment);

    List<Comment> getItemComments(Item item);
}
