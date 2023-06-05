package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntCommentServiceTest {
    private final EntityManager em;
    private final CommentService service;

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
    private final List<Comment> sourceComments = List.of(
            Comment.builder()
                    .text("test comment 1")
                    .build(),
            Comment.builder()
                    .text("test comment 2")
                    .build(),
            Comment.builder()
                    .text("test comment 3")
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

        for (int i = 0; i < sourceComments.size(); i++) {
            Comment comment = sourceComments.get(i);
            if (i == 0) {
                comment.setAuthor(user3);
                comment.setItem(sourceItems.get(0));
            }
            if (i == 1) {
                comment.setAuthor(user3);
                comment.setItem(sourceItems.get(1));
            }
            if (i == 2) {
                comment.setAuthor(user3);
                comment.setItem(sourceItems.get(1));
            }
            em.persist(comment);
        }
        em.flush();
    }

    @Test
    void createComment() {
        long stopSec = 2L;
        Comment comment4 = Comment.builder().text("test comment 4")
                .author(sourceUsers.get(2))
                .item(sourceItems.get(1))
                .build();
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusSeconds(stopSec))
                .end(LocalDateTime.of(2029, 1, 2, 10, 0))
                .booker(sourceUsers.get(2))
                .item(sourceItems.get(1))
                .status(BookingStatus.APPROVED)
                .build();
        em.persist(booking);
        em.flush();

        Assertions.assertThrows(
                ValidationException.class,
                () -> service.createComment(comment4));

        try {
            TimeUnit time = TimeUnit.SECONDS;
            time.sleep(stopSec);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        service.createComment(comment4);

        TypedQuery<Comment> query = em.createQuery("Select u from Comment u where u.text = :text", Comment.class);
        Comment comment = query.setParameter("text", comment4.getText())
                .getSingleResult();

        assertThat(comment.getId(), notNullValue());
        assertThat(comment.getText(), equalTo(comment4.getText()));
        assertThat(comment.getItem(), equalTo(comment4.getItem()));
        assertThat(comment.getAuthor(), equalTo(comment4.getAuthor()));
    }

    @Test
    void getItemComments() {
        Item item = sourceItems.get(1);
        List<Comment> targetComments = service.getItemComments(item);

        assertThat(targetComments, hasSize(2));
        for (Comment sourceComment : targetComments) {
            assertThat(targetComments, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("text", equalTo(sourceComment.getText())),
                    hasProperty("item", equalTo(sourceComment.getItem())),
                    hasProperty("author", equalTo(sourceComment.getAuthor()))
            )));
        }
    }
}
