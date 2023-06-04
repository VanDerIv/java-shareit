package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntItemRequestServiceTest {
    private final EntityManager em;
    private final ItemRequestService service;
    private final List<User> sourceUsers = List.of(
            User.builder().name("test1").email("test1@test.ru").build(),
            User.builder().name("test2").email("test2@test.ru").build(),
            User.builder().name("test3").email("test3@test.ru").build()
    );
    private final List<Item> sourceItems = List.of(
            Item.builder()
                    .name("test item 1")
                    .description("test item 1")
                    .available(true)
                    .build(),
            Item.builder()
                    .name("test item 2")
                    .description("test item 2")
                    .available(true)
                    .build(),
            Item.builder()
                    .name("test item 3")
                    .description("test item 3")
                    .available(true)
                    .build(),
            Item.builder()
                    .name("test item 4")
                    .description("test item 4")
                    .available(true)
                    .build()
    );
    private final List<ItemRequest> sourceItemRequests = List.of(
            ItemRequest.builder()
                    .description("Хочу вещь 1")
                    .created(LocalDateTime.now())
                    .build(),
            ItemRequest.builder()
                    .description("Хочу вещь 2")
                    .created(LocalDateTime.now())
                    .build(),
            ItemRequest.builder()
                    .description("Хочу вещь 3")
                    .created(LocalDateTime.now())
                    .build()
    );

    @BeforeEach
    public void createEntities() {
        for (User user : sourceUsers) {
            em.persist(user);
        }
        em.flush();
        User user1 = sourceUsers.get(0);
        User user2 = sourceUsers.get(1);
        User user3 = sourceUsers.get(2);

        for (int i = 0; i < sourceItems.size(); i++) {
            Item item = sourceItems.get(i);
            if (i == 0) item.setOwner(user1);
            if (i == 1) item.setOwner(user2);
            if (i == 2) item.setOwner(user2);
            if (i == 3) item.setOwner(user2);
            em.persist(item);
        }
        em.flush();

        for (int i = 0; i < sourceItemRequests.size(); i++) {
            ItemRequest itemRequest = sourceItemRequests.get(i);
            if (i == 0) itemRequest.setRequestor(user2);
            if (i == 1) itemRequest.setRequestor(user3);
            if (i == 2) itemRequest.setRequestor(user3);
            em.persist(itemRequest);
        }
        em.flush();
    }

    @Test
    void getItemComments() {
        List<ItemRequest> targetItemRequests = service.getUserRequests(sourceUsers.get(2));

        assertThat(targetItemRequests, hasSize(2));
        for (ItemRequest targetItemRequest : targetItemRequests) {
            assertThat(targetItemRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(targetItemRequest.getDescription())),
                    hasProperty("requestor", equalTo(targetItemRequest.getRequestor())),
                    hasProperty("created", notNullValue()))
            ));
        }
    }
}
