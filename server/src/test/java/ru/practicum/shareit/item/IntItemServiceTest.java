package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class IntItemServiceTest {
    private final EntityManager em;
    private final ItemService service;

    private final List<User> sourceUsers = List.of(
            User.builder().name("test1").email("test1@test.ru").build(),
            User.builder().name("test2").email("test2@test.ru").build(),
            User.builder().name("test3").email("test3@test.ru").build()
    );
    private final List<Item> sourceItems = List.of(
            Item.builder()
                    .name("test item 1")
                    .description("test item aAaA 1")
                    .available(true)
                    .build(),
            Item.builder()
                    .name("test item 2")
                    .description("test item aa 2")
                    .available(true)
                    .build(),
            Item.builder()
                    .name("test item 3")
                    .description("test item bbb 3")
                    .available(true)
                    .build(),
            Item.builder()
                    .name("test item AA 4")
                    .description("test item ccc 4")
                    .available(true)
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

        for (int i = 0; i < sourceItems.size(); i++) {
            Item item = sourceItems.get(i);
            if (i == 0) item.setOwner(user1);
            if (i == 1) item.setOwner(user2);
            if (i == 2) item.setOwner(user2);
            if (i == 3) item.setOwner(user2);
            em.persist(item);
        }
        em.flush();
    }

    @Test
    void getUserItems() {
        User user2 = sourceUsers.get(1);
        List<Item> targetItems = service.getUserItems(user2, 0, 10);

        assertThat(targetItems, hasSize(3));
        for (Item sourceItem : targetItems) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceItem.getName())),
                    hasProperty("description", equalTo(sourceItem.getDescription())),
                    hasProperty("available", equalTo(sourceItem.getAvailable())),
                    hasProperty("owner", equalTo(sourceItem.getOwner()))
            )));
        }
    }

    @Test
    void findAvailableItems() {
        User user2 = sourceUsers.get(1);
        List<Item> targetItems = service.findAvailableItems(user2, "aa", 0, 10);
        assertThat(targetItems, hasSize(3));
    }

    @Test
    void createItem() {
        Item item5 = Item.builder()
                .name("test item 5")
                .description("test item 5")
                .available(true)
                .owner(sourceUsers.get(2))
                .build();

        service.createItem(item5);

        TypedQuery<Item> query = em.createQuery("Select u from Item u where u.name = :name", Item.class);
        Item item = query.setParameter("name", item5.getName())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(item5.getName()));
        assertThat(item.getDescription(), equalTo(item5.getDescription()));
        assertThat(item.getAvailable(), equalTo(item5.getAvailable()));
        assertThat(item.getOwner(), equalTo(item5.getOwner()));

        ItemRequest itemRequest = ItemRequest.builder()
                .description("Хочу вещь")
                .requestor(sourceUsers.get(2))
                .build();
        em.persist(itemRequest);

        Item item6 = Item.builder()
                .name("test item 6")
                .description("test item 6")
                .available(true)
                .owner(sourceUsers.get(1))
                .request(itemRequest)
                .build();
        em.persist(item6);
        em.flush();

        query = em.createQuery("Select u from Item u where u.name = :name", Item.class);
        item = query.setParameter("name", item6.getName())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(item6.getName()));
        assertThat(item.getDescription(), equalTo(item6.getDescription()));
        assertThat(item.getAvailable(), equalTo(item6.getAvailable()));
        assertThat(item.getOwner(), equalTo(item6.getOwner()));
        assertThat(item.getRequest(), equalTo(item6.getRequest()));
    }
}
