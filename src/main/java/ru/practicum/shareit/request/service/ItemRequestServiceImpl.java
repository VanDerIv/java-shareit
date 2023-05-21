package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemRequest createRequest(ItemRequest itemRequest) {
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequest> getUserRequests(User user) {
        return itemRequestRepository.findByRequestor(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequest> getOtherRequests(User user, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemRequestRepository.findByRequestorNotOrderByIdDesc(user, page);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequest getRequest(Long id) {
        Optional<ItemRequest> optionalRequest = itemRequestRepository.findById(id);
        return optionalRequest.orElseThrow(() -> new NotFoundException(String.format("Запрос с id=%d не найден", id)));
    }
}
