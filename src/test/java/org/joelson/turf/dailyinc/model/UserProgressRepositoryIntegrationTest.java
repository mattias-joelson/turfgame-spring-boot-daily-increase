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
    private static final List<UserProgress> SORTED_USER_PROGRESS_REVERSED = SORTED_USER_PROGRESS.reversed();
    private static final List<UserProgress> USER_ONE_SORTED_USER_PROGRESS = List.of(USER_ONE_PROGRESS,
            USER_ONE_NEXT_PROGRESS);
    private static final List<UserProgress> USER_ONE_SORTED_USER_PROGRESS_REVERSED = USER_ONE_SORTED_USER_PROGRESS.reversed();
    private static final List<UserProgress> USER_TWO_SORTED_USER_PROGRESS = List.of(USER_TWO_PROGRESS,
            USER_TWO_NEXT_PROGRESS);
    private static final List<UserProgress> USER_TWO_SORTED_USER_PROGRESS_REVERSED = USER_TWO_SORTED_USER_PROGRESS.reversed();

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
                new ProgressId(USER_ONE_PROGRESS.getUser().getId(), USER_ONE_PROGRESS.getDate())).orElse(null);
        assertEquals(USER_ONE_PROGRESS, userProgress);
        assertEquals(USER_ONE_NEXT_PROGRESS,
                userProgressRepository.findById(new ProgressId(USER_ONE.getId(), NEXT_DATE)).orElse(null));
        assertEquals(USER_TWO_PROGRESS,
                userProgressRepository.findById(new ProgressId(USER_TWO.getId(), DATE)).orElse(null));
        assertEquals(USER_TWO_NEXT_PROGRESS,
                userProgressRepository.findById(new ProgressId(USER_TWO.getId(), NEXT_DATE)).orElse(null));

        assertNull(
                userProgressRepository.findById(new ProgressId(1000L, USER_ONE_PROGRESS.getDate())).orElse(null));
        assertNull(userProgressRepository.findById(
                new ProgressId(USER_ONE.getId(), NEXT_DATE.plus(1, ChronoUnit.DAYS))).orElse(null));
    }

    @Test
    public void givenNewUserProgress_whenSave_thenSaved() {
        entityManager.persist(USER_ONE);

        UserProgress savedUserProgress = userProgressRepository.save(USER_ONE_PROGRESS);
        assertEquals(USER_ONE_PROGRESS, entityManager.find(UserProgress.class,
                new ProgressId(savedUserProgress.getUser().getId(), savedUserProgress.getDate())));

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
                new ProgressId(savedUserProgress.getUser().getId(), savedUserProgress.getDate())));
    }

    @Test
    public void givenUserProgress_whenFindSortedBetween_thenListReturned() {
        entityManager.persist(USER_TWO_NEXT_PROGRESS);
        entityManager.persist(USER_TWO_PROGRESS);
        entityManager.persist(USER_ONE_NEXT_PROGRESS);
        entityManager.persist(USER_ONE_PROGRESS);

        assertEquals(SORTED_USER_PROGRESS, userProgressRepository.findSortedBetween(0, SORTED_USER_PROGRESS.size(), UserProgress.class));
        assertEquals(SORTED_USER_PROGRESS.subList(1, 3), userProgressRepository.findSortedBetween(1, 2, UserProgress.class));
        assertEquals(List.of(), userProgressRepository.findSortedBetween(SORTED_USER_PROGRESS.size(), 100, UserProgress.class));

        assertEquals(SORTED_USER_PROGRESS_REVERSED,
                userProgressRepository.findLastSortedReversed(SORTED_USER_PROGRESS_REVERSED.size(), UserProgress.class));
        assertEquals(SORTED_USER_PROGRESS_REVERSED.subList(0, 2),
                userProgressRepository.findLastSortedReversed(2, UserProgress.class));
        assertEquals(List.of(), userProgressRepository.findLastSortedReversed(0, UserProgress.class));
    }

    @Test
    public void givenUserProgress_whenFindAllSortedByUser_thenListReturned() {
        entityManager.persist(USER_TWO_NEXT_PROGRESS);
        entityManager.persist(USER_TWO_PROGRESS);
        entityManager.persist(USER_ONE_NEXT_PROGRESS);
        entityManager.persist(USER_ONE_PROGRESS);

        assertEquals(USER_ONE_SORTED_USER_PROGRESS,
                userProgressRepository.findSortedBetweenByUser(USER_ONE.getId(), 0, USER_ONE_SORTED_USER_PROGRESS.size(), UserProgress.class));
        assertEquals(USER_TWO_SORTED_USER_PROGRESS,
                userProgressRepository.findSortedBetweenByUser(USER_TWO.getId(), 0, USER_TWO_SORTED_USER_PROGRESS.size(), UserProgress.class));
        assertEquals(List.of(), userProgressRepository.findSortedBetweenByUser(1003L, 0, 100, UserProgress.class));

        assertEquals(USER_ONE_SORTED_USER_PROGRESS_REVERSED,
                userProgressRepository.findLastSortedReversedByUser(USER_ONE.getId(), USER_ONE_SORTED_USER_PROGRESS_REVERSED.size(), UserProgress.class));
        assertEquals(USER_ONE_SORTED_USER_PROGRESS_REVERSED.subList(0, 1),
                userProgressRepository.findLastSortedReversedByUser(USER_ONE.getId(), 1, UserProgress.class));
        assertEquals(List.of(), userProgressRepository.findLastSortedReversedByUser(USER_ONE.getId(), 0, UserProgress.class));
        assertEquals(USER_TWO_SORTED_USER_PROGRESS_REVERSED,
                userProgressRepository.findLastSortedReversedByUser(USER_TWO.getId(), USER_TWO_SORTED_USER_PROGRESS_REVERSED.size(), UserProgress.class));
        assertEquals(List.of(), userProgressRepository.findLastSortedReversedByUser(1003L, 100, UserProgress.class));
    }
}
