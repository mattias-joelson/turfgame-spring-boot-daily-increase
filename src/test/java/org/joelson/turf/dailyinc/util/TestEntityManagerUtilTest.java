package org.joelson.turf.dailyinc.util;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.UserRepository;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class TestEntityManagerUtilTest {

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);

    @Autowired
    UserRepository userRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void givenUserList_whenPersistList_thenListAreEqual() {
        List<User> usersToPersist = ListTestUtil.createList(1001L, 2001L, 100L, id -> new User(id, "User" + id, TIME));
        assertFalse(usersToPersist.isEmpty());
        for (User u : usersToPersist) {
            assertNull(entityManager.find(User.class, u.getId()));
        }

        List<User> users = TestEntityManagerUtil.persistList(entityManager, usersToPersist);
        assertEquals(usersToPersist.size(), users.size());
        TestEntityManagerUtil.verifyPersistedList(entityManager, User.class, usersToPersist, User::getId);
        TestEntityManagerUtil.verifyPersistedList(entityManager, User.class, users, User::getId);
    }

    @Test
    public void givenSavedUsers_whenVerifyPersisted_thenSuccess() {
        List<User> usersToPersist = ListTestUtil.createList(1001L, 2001L, 100L, id -> new User(id, "User" + id, TIME));
        assertFalse(usersToPersist.isEmpty());
        List<User> users = usersToPersist.stream().map(userRepository::save).toList();
        assertEquals(usersToPersist.size(), users.size());

        TestEntityManagerUtil.verifyPersistedList(entityManager, User.class, users, User::getId);
    }

    @Test
    public void givenNoUsers_whenVerifyPersisted_thenAssertionFailed() {
        List<User> users = ListTestUtil.createList(1001L, 2001L, 100L, id -> new User(id, "User" + id, TIME));
        assertFalse(users.isEmpty());

        assertThrows(AssertionFailedError.class,
                () -> TestEntityManagerUtil.verifyPersistedList(entityManager, User.class, users, User::getId));
    }
}
