package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequestor(User requestor);

    @Query("select r from ItemRequest r where r.requestor <> ?1 order by r.id DESC")
    List<ItemRequest> findByRequestorNotOrderByIdDesc(User requestor, Pageable pageable);
}
