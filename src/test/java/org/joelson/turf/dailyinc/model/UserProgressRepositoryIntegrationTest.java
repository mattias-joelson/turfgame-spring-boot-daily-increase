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
public class UserProgressRepositoryIntegrationTest {

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private static final Instant NEXT_TIME = TIME.plus(1, ChronoUnit.DAYS);

    private static final Instant DATE = TIME.truncatedTo(ChronoUnit.DAYS);
    private static final Instant NEXT_DATE = DATE.plus(1, ChronoUnit.DAYS);

    private static final User USER_ONE = new User(1001L, "UserOne", NEXT_TIME);
    private static final UserProgress USER_ONE_PROGRESS = new UserProgress(USER_ONE, DATE, 1,
            new UserProgressTypeProgress(0, 1, TIME), new UserProgressTypeProgress(0, 1, TIME),
            new UserProgressTypeProgress(0, 1, TIME), new UserProgressTypeProgress(0, 1, TIME));
    private static final UserProgress USER_ONE_NEXT_PROGRESS = new UserProgress(USER_ONE, NEXT_DATE, 3,
            new UserProgressTypeProgress(1, 2, NEXT_TIME), new UserProgressTypeProgress(1, 2, NEXT_TIME),
            new UserProgressTypeProgress(1, 2, NEXT_TIME), new UserProgressTypeProgress(1, 2, NEXT_TIME));

    private static final User USER_TWO = new User(1002L, "UserTwo", NEXT_TIME);
    private static final UserProgress USER_TWO_PROGRESS = new UserProgress(USER_TWO, DATE, 10,
            new UserProgressTypeProgress(10, 10, TIME), new UserProgressTypeProgress(4, 4, TIME),
            new UserProgressTypeProgress(6, 6, TIME), new UserProgressTypeProgress(4, 4, TIME));
    private static final UserProgress USER_TWO_NEXT_PROGRESS = new UserProgress(USER_TWO, NEXT_DATE, 13,
            new UserProgressTypeProgress(10, 11, NEXT_TIME), new UserProgressTypeProgress(4, 4, NEXT_TIME),
            new UserProgressTypeProgress(6, 7, NEXT_TIME), new UserProgressTypeProgress(4, 4, NEXT_TIME));

    private static final List<UserProgress> SORTED_USER_PROGRESS = List.of(USER_ONE_PROGRESS, USER_ONE_NEXT_PROGRESS,
            USER_TWO_PROGRESS, USER_TWO_NEXT_PROGRESS);
    private static final List<UserProgress> USER_ONE_SORTED_USER_PROGRESS = List.of(USER_ONE_PROGRESS,
            USER_ONE_NEXT_PROGRESS);
    private static final List<UserProgress> USER_TWO_SORTED_USER_PROGRESS = List.of(USER_TWO_PROGRESS,
            USER_TWO_NEXT_PROGRESS);

    @Autowired
    UserProgressRepository userProgressRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void withUserProgress_whenFindById_thenExistingReturned() {
        entityManager.persist(USER_ONE);
        entityManager.persist(USER_TWO);
        entityManager.persist(USER_ONE_PROGRESS);
        entityManager.persist(USER_ONE_NEXT_PROGRESS);
        entityManager.persist(USER_TWO_PROGRESS);
        entityManager.persist(USER_TWO_NEXT_PROGRESS);

        UserProgress userProgress = userProgressRepository.findById(
                new UserProgressId(USER_ONE_PROGRESS.getUser().getId(), USER_ONE_PROGRESS.getDate())).orElse(null);
        assertEquals(USER_ONE_PROGRESS, userProgress);
        assertEquals(USER_ONE_NEXT_PROGRESS,
                userProgressRepository.findById(new UserProgressId(USER_ONE.getId(), NEXT_DATE)).orElse(null));
        assertEquals(USER_TWO_PROGRESS,
                userProgressRepository.findById(new UserProgressId(USER_TWO.getId(), DATE)).orElse(null));
        assertEquals(USER_TWO_NEXT_PROGRESS,
                userProgressRepository.findById(new UserProgressId(USER_TWO.getId(), NEXT_DATE)).orElse(null));

        assertNull(
                userProgressRepository.findById(new UserProgressId(1000L, USER_ONE_PROGRESS.getDate())).orElse(null));
        assertNull(userProgressRepository.findById(
                new UserProgressId(USER_ONE.getId(), NEXT_DATE.plus(1, ChronoUnit.DAYS))).orElse(null));
    }

    @Test
    public void givenNewUserProgress_whenSave_thenSaved() {
        entityManager.persist(USER_ONE);

        UserProgress savedUserProgress = userProgressRepository.save(USER_ONE_PROGRESS);
        assertEquals(USER_ONE_PROGRESS, entityManager.find(UserProgress.class,
                new UserProgressId(savedUserProgress.getUser().getId(), savedUserProgress.getDate())));

        assertThrows(EntityExistsException.class, () -> entityManager.persist(USER_ONE_PROGRESS));
    }

    @Test
    public void givenUserProgress_whenUpdate_thenUpdated() {
        UserProgress userProgress = new UserProgress(USER_TWO, DATE, 10, new UserProgressTypeProgress(10, 10, TIME),
                new UserProgressTypeProgress(4, 4, TIME), new UserProgressTypeProgress(6, 6, TIME),
                new UserProgressTypeProgress(4, 4, TIME));
        entityManager.persist(userProgress);

        userProgress.getIncrease().setCompleted(userProgress.getIncrease().getCompleted() + 1);
        userProgress.getIncrease().setTime(TIME.plusSeconds(60));
        UserProgress savedUserProgress = userProgressRepository.save(userProgress);
        assertEquals(userProgress, entityManager.find(UserProgress.class,
                new UserProgressId(savedUserProgress.getUser().getId(), savedUserProgress.getDate())));
    }

    @Test
    public void givenUserProgress_whenFindAllSorted_thenAllReturned() {
        entityManager.persist(USER_TWO_NEXT_PROGRESS);
        entityManager.persist(USER_TWO_PROGRESS);
        entityManager.persist(USER_ONE_NEXT_PROGRESS);
        entityManager.persist(USER_ONE_PROGRESS);

        assertEquals(SORTED_USER_PROGRESS, userProgressRepository.findAllSorted(UserProgress.class));
    }

    @Test
    public void givenUserProgress_whenFindAllSortedByUser_thenListReturned() {
        entityManager.persist(USER_TWO_NEXT_PROGRESS);
        entityManager.persist(USER_TWO_PROGRESS);
        entityManager.persist(USER_ONE_NEXT_PROGRESS);
        entityManager.persist(USER_ONE_PROGRESS);

        assertEquals(USER_ONE_SORTED_USER_PROGRESS,
                userProgressRepository.findAllSortedByUser(USER_ONE.getId(), UserProgress.class));
        assertEquals(USER_TWO_SORTED_USER_PROGRESS,
                userProgressRepository.findAllSortedByUser(USER_TWO.getId(), UserProgress.class));
        assertEquals(List.of(), userProgressRepository.findAllSortedByUser(1003L, UserProgress.class));
    }
}
