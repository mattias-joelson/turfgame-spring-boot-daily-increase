package org.joelson.turf.dailyinc.model;

import jakarta.persistence.EntityExistsException;
import org.joelson.turf.dailyinc.util.ListTestUtil;
import org.joelson.turf.dailyinc.util.TestEntityManagerUtil;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepositoryIntegrationTest {

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private static final User USER_ONE = new User(1001L, "UserOne", TIME);
    private static final User USER_TWO = new User(1002L, "UserTwo", TIME);

    @Autowired
    UserRepository userRepository;

    @Autowired
    TestEntityManager entityManager;

    private static void verifyUserList(
            List<User> users, long minId, long maxId, long stepId, int minSize, int maxSize) {
        ListTestUtil.verifyList(users, minId, maxId, stepId, minSize, maxSize, User::getId);
    }

    private int persistUsers(long minId, long maxId, long stepId) {
        return TestEntityManagerUtil.persistList(entityManager, minId, maxId, stepId,
                id -> new User(id, "User" + id, TIME)).size();
    }

    @Test
    public void givenUsers_whenFindByIdRaw_thenExistingReturned() {
        entityManager.persist(USER_TWO);
        entityManager.persist(USER_ONE);

        assertEquals(USER_TWO, userRepository.findById(USER_TWO.getId()).orElse(null));
        assertNull(userRepository.findById(4711L).orElse(null));
    }

    @Test
    public void givenNewUser_whenSave_thenSaved() {
        User savedUser = userRepository.save(USER_ONE);
        assertEquals(USER_ONE, entityManager.find(User.class, savedUser.getId()));

        assertThrows(EntityExistsException.class, () -> entityManager.persist(USER_ONE));
    }

    @Test
    public void givenUserCreated_whenUpdate_thenUpdated() {
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
    public void givenFewUsersInRange_whenFindSortedBetween_thenAllReturned() {
        long minId = 1500L;
        long maxId = 2500L;
        long stepId = 100L;
        int created = persistUsers(minId, maxId, stepId);
        int limit = 100;
        assertTrue(created < limit);

        List<User> users = userRepository.findSortedBetween(minId, maxId, limit, User.class);
        verifyUserList(users, minId, maxId, stepId, created, created);
    }

    @Test
    public void givenMoreUsersInRangeThanLimit_whenFindSortedBetween_thenLimitReturned() {
        long minId = 1001L;
        long maxId = 3001L;
        long stepId = 10L;
        int created = persistUsers(minId, maxId, stepId);
        int limit = 100;
        assertTrue(created > limit);

        List<User> users = userRepository.findSortedBetween(minId, maxId, limit, User.class);
        verifyUserList(users, minId, maxId, stepId, limit, limit);
    }

    @Test
    public void givenUsersOutsideOfRange_whenFindSortedBetween_thenNoneReturned() {
        int created = persistUsers(3001L, 4001L, 10);
        assertTrue(created > 0);

        List<User> users = userRepository.findSortedBetween(1500L, 2500L, 100, User.class);
        assertTrue(users.isEmpty());
    }

    @Test
    public void givenFewUsers_whenFindLastSortedReversed_thenAllReturned() {
        long minId = 1001L;
        long maxId = 2001L;
        long stepId = 100L;
        int created = persistUsers(minId, maxId, stepId);
        assertTrue(created < 100);

        List<User> users = userRepository.findLastSortedReversed(100, User.class);
        verifyUserList(users, minId, maxId, -stepId, created, created);
    }

    @Test
    public void givenManyUsers_whenFindLastSortedReversed_thenLimitReturned() {
        long minId = 1001L;
        long maxId = 2001L;
        long stepId = 10L;
        int created = persistUsers(minId, maxId, stepId);
        int limit = 10;
        assertTrue(created > limit);

        List<User> users = userRepository.findLastSortedReversed(limit, User.class);
        verifyUserList(users, minId, maxId, -stepId, limit, limit);
    }

    @Test
    public void givenNoUsers_whenFindLastSortedReversed_thenNoneReturned() {
        List<User> users = userRepository.findLastSortedReversed(10, User.class);
        assertTrue(users.isEmpty());
    }

    @Test
    public void givenUsers_whenFindById_thenExistingReturned() {
        entityManager.persist(USER_TWO);
        entityManager.persist(USER_ONE);

        assertEquals(USER_ONE, userRepository.findById(USER_ONE.getId(), User.class).orElse(null));
        assertEquals(USER_TWO, userRepository.findById(USER_TWO.getId(), User.class).orElse(null));
        assertNull(userRepository.findById(4711L, User.class).orElse(null));
    }

    @Test
    public void givenUsers_whenFindByName_thenExistingReturned() {
        entityManager.persist(USER_TWO);
        entityManager.persist(USER_ONE);

        assertEquals(USER_ONE, userRepository.findByName(USER_ONE.getName(), User.class).orElse(null));
        assertEquals(USER_TWO, userRepository.findByName(USER_TWO.getName(), User.class).orElse(null));
        assertNull(userRepository.findByName("TestUser", User.class).orElse(null));
    }
}
