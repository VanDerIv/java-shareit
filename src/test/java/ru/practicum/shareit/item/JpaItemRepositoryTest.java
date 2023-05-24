package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
public class JpaItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository repository;

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
                    .build(),
            Item.builder()
                    .name("test item 5")
                    .description("test item yyyyy 5")
                    .available(false)
                    .build()
    );

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    public void findByAvailableTrueAndNameContainsIgnoreCaseOrDescriptionContainsIgnoreCase() {
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
            if (i == 4) item.setOwner(user3);
            em.persist(item);
        }
        em.flush();

        PageRequest page = PageRequest.of(0, 10);
        List<Item> items = repository.findByAvailableTrueAndNameContainsIgnoreCaseOrDescriptionContainsIgnoreCase("", page);
        assertThat(items, hasSize(4));

        items = repository.findByAvailableTrueAndNameContainsIgnoreCaseOrDescriptionContainsIgnoreCase("aa", page);
        assertThat(items, hasSize(3));

        items = repository.findByAvailableTrueAndNameContainsIgnoreCaseOrDescriptionContainsIgnoreCase("cCc", page);
        assertThat(items, hasSize(1));

        items = repository.findByAvailableTrueAndNameContainsIgnoreCaseOrDescriptionContainsIgnoreCase("yyyyy", page);
        assertThat(items, hasSize(0));
    }
}
