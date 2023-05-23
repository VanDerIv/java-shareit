package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
public class IntUserServiceTest {
    private final EntityManager em;
    private final UserService service;

    private List<User> sourceUsers = List.of(
            User.builder().name("test1").email("test1@test.ru").build(),
            User.builder().name("test2").email("test2@test.ru").build(),
            User.builder().name("test3").email("test3@test.ru").build()
    );

    @BeforeEach
    public void createEntities() {
        for (User user : sourceUsers) {
            em.persist(user);
        }
        em.flush();

        TypedQuery<User> query = em.createQuery("Select u from User u", User.class);
        sourceUsers = query.getResultList();
    }

    @Test
    void getUsers() {
        List<User> targetUsers = service.getUsers();

        assertThat(targetUsers, hasSize(sourceUsers.size()));
        for (User sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem( allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    void getUser() {
        User user1 = service.getUser(sourceUsers.get(0).getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", user1.getId())
                .getSingleResult();

        assertThat(user.getName(), equalTo(user1.getName()));
        assertThat(user.getEmail(), equalTo(user1.getEmail()));
    }

    @Test
    void createUser() {
        User user4 = User.builder().name("test4").email("test4@test.ru").build();

        service.createUser(user4);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", user4.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(user4.getName()));
        assertThat(user.getEmail(), equalTo(user4.getEmail()));
    }

    @Test
    void updateUser() {
        User user3 = User.builder().id(sourceUsers.get(2).getId()).name("test33").email("test3@test.ru").build();

        service.updateUser(user3, sourceUsers.get(2).getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", user3.getId())
                .getSingleResult();

        assertThat(user.getName(), equalTo(user3.getName()));
        assertThat(user.getEmail(), equalTo(user3.getEmail()));
    }

    @Test
    void deleteUser() {
        service.deleteUser(sourceUsers.get(2).getId());
        User user = em.find(User.class, sourceUsers.get(2).getId());
        assertThat(user, nullValue());
    }
}
