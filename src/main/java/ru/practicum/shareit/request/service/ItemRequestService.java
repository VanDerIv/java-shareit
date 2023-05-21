package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestService {
    ItemRequest createRequest(ItemRequest itemRequest);

    List<ItemRequest> getUserRequests(User user);

    List<ItemRequest> getOtherRequests(User user, Integer from, Integer size);

    ItemRequest getRequest(Long id);
}
