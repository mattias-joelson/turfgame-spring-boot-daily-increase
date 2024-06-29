package org.joelson.turf.dailyinc.model;

import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class UserRepositoryIntegrationTest {

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    public static final User USER_ONE = new User(1L, "UserOne", TIME);
    public static final User USER_TWO = new User(2L, "UserTwo", TIME);

    @Autowired
    UserRepository userRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void givenUsers_whenFindByIdRaw_thenSuccess() {
        entityManager.persist(USER_TWO);
        entityManager.persist(USER_ONE);

        User foundUser = userRepository.findById(USER_TWO.getId()).orElse(null);
        assertEquals(USER_TWO, foundUser);
        assertNull(userRepository.findById(4711L).orElse(null));
    }

    @Test
    public void givenNewUser_whenSave_thenSuccess() {
        User savedUser = userRepository.save(USER_ONE);
        assertEquals(USER_ONE, entityManager.find(User.class, savedUser.getId()));

        assertThrows(EntityExistsException.class, () -> entityManager.persist(USER_ONE));
    }

    @Test
    public void givenUserCreated_whenUpdate_thenSuccess() {
        User newUser = new User(1L, "User", TIME);
        entityManager.persist(newUser);

        String newName = "UserNew";
        Instant newTime = TIME.plusSeconds(1);
        newUser.setName(newName);
        newUser.setTime(newTime);
        userRepository.save(newUser);
        assertEquals(newUser, entityManager.find(User.class, newUser.getId()));
    }

    @Test
    public void givenUsers_whenFindAllSorted_thenSuccess() {
        entityManager.persist(USER_TWO);
        entityManager.persist(USER_ONE);

        List<User> sortedUsers = userRepository.findAllSorted(User.class);
        assertEquals(USER_ONE, sortedUsers.getFirst());
        assertEquals(USER_TWO, sortedUsers.getLast());
    }

    @Test
    public void givenUsers_whenFindById_thenSuccess() {
        entityManager.persist(USER_TWO);
        entityManager.persist(USER_ONE);

        User foundUser = userRepository.findById(USER_TWO.getId(), User.class).orElse(null);
        assertEquals(USER_TWO, foundUser);
        assertNull(userRepository.findById(4711L, User.class).orElse(null));
    }

    @Test
    public void givenUsers_whenFindByName_thenSuccess() {
        entityManager.persist(USER_TWO);
        entityManager.persist(USER_ONE);

        User foundUser = userRepository.findByName(USER_TWO.getName(), User.class).orElse(null);
        assertEquals(USER_TWO, foundUser);
        assertNull(userRepository.findByName("TestUser", User.class).orElse(null));
    }
}
