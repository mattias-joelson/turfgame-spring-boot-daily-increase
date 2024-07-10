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
    private static final Progress USER_ONE_PROGRESS = new Progress(USER_ONE, DATE, 1, new DailyProgress(0, 1, TIME),
            new DailyProgress(0, 1, TIME), new DailyProgress(0, 1, TIME), new DailyProgress(0, 1, TIME));
    private static final Progress USER_ONE_NEXT_PROGRESS = new Progress(USER_ONE, NEXT_DATE, 3,
            new DailyProgress(1, 2, NEXT_TIME), new DailyProgress(1, 2, NEXT_TIME), new DailyProgress(1, 2, NEXT_TIME),
            new DailyProgress(1, 2, NEXT_TIME));

    private static final User USER_TWO = new User(1002L, "UserTwo", NEXT_TIME);
    private static final Progress USER_TWO_PROGRESS = new Progress(USER_TWO, DATE, 10, new DailyProgress(10, 10, TIME),
            new DailyProgress(4, 4, TIME), new DailyProgress(6, 6, TIME), new DailyProgress(4, 4, TIME));
    private static final Progress USER_TWO_NEXT_PROGRESS = new Progress(USER_TWO, NEXT_DATE, 13,
            new DailyProgress(10, 11, NEXT_TIME), new DailyProgress(4, 4, NEXT_TIME),
            new DailyProgress(6, 7, NEXT_TIME), new DailyProgress(4, 4, NEXT_TIME));

    private static final List<Progress> SORTED_PROGRESS = List.of(USER_ONE_PROGRESS, USER_ONE_NEXT_PROGRESS,
            USER_TWO_PROGRESS, USER_TWO_NEXT_PROGRESS);
    private static final List<Progress> SORTED_PROGRESS_REVERSED = SORTED_PROGRESS.reversed();
    private static final List<Progress> USER_ONE_SORTED_PROGRESS = List.of(USER_ONE_PROGRESS, USER_ONE_NEXT_PROGRESS);
    private static final List<Progress> USER_ONE_SORTED_PROGRESS_REVERSED = USER_ONE_SORTED_PROGRESS.reversed();
    private static final List<Progress> USER_TWO_SORTED_PROGRESS = List.of(USER_TWO_PROGRESS, USER_TWO_NEXT_PROGRESS);
    private static final List<Progress> USER_TWO_SORTED_PROGRESS_REVERSED = USER_TWO_SORTED_PROGRESS.reversed();

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

        Progress progress = userProgressRepository.findById(
                new ProgressId(USER_ONE_PROGRESS.getUser().getId(), USER_ONE_PROGRESS.getDate())).orElse(null);
        assertEquals(USER_ONE_PROGRESS, progress);
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

        Progress savedProgress = userProgressRepository.save(USER_ONE_PROGRESS);
        assertEquals(USER_ONE_PROGRESS, entityManager.find(Progress.class,
                new ProgressId(savedProgress.getUser().getId(), savedProgress.getDate())));

        assertThrows(EntityExistsException.class, () -> entityManager.persist(USER_ONE_PROGRESS));
    }

    @Test
    public void givenUserProgress_whenUpdate_thenUpdated() {
        Progress progress = new Progress(USER_TWO, DATE, 10, new DailyProgress(10, 10, TIME),
                new DailyProgress(4, 4, TIME), new DailyProgress(6, 6, TIME), new DailyProgress(4, 4, TIME));
        entityManager.persist(progress);

        progress.getIncrease().setCompleted(progress.getIncrease().getCompleted() + 1);
        progress.getIncrease().setTime(TIME.plusSeconds(60));
        Progress savedProgress = userProgressRepository.save(progress);
        assertEquals(progress, entityManager.find(Progress.class,
                new ProgressId(savedProgress.getUser().getId(), savedProgress.getDate())));
    }

    @Test
    public void givenUserProgress_whenFindSortedBetween_thenListReturned() {
        entityManager.persist(USER_TWO_NEXT_PROGRESS);
        entityManager.persist(USER_TWO_PROGRESS);
        entityManager.persist(USER_ONE_NEXT_PROGRESS);
        entityManager.persist(USER_ONE_PROGRESS);

        assertEquals(SORTED_PROGRESS, userProgressRepository.findSortedBetween(0, SORTED_PROGRESS.size(), Progress.class));
        assertEquals(SORTED_PROGRESS.subList(1, 3), userProgressRepository.findSortedBetween(1, 2, Progress.class));
        assertEquals(List.of(), userProgressRepository.findSortedBetween(SORTED_PROGRESS.size(), 100, Progress.class));

        assertEquals(SORTED_PROGRESS_REVERSED,
                userProgressRepository.findLastSortedReversed(SORTED_PROGRESS_REVERSED.size(), Progress.class));
        assertEquals(SORTED_PROGRESS_REVERSED.subList(0, 2),
                userProgressRepository.findLastSortedReversed(2, Progress.class));
        assertEquals(List.of(), userProgressRepository.findLastSortedReversed(0, Progress.class));
    }

    @Test
    public void givenUserProgress_whenFindAllSortedByUser_thenListReturned() {
        entityManager.persist(USER_TWO_NEXT_PROGRESS);
        entityManager.persist(USER_TWO_PROGRESS);
        entityManager.persist(USER_ONE_NEXT_PROGRESS);
        entityManager.persist(USER_ONE_PROGRESS);

        assertEquals(USER_ONE_SORTED_PROGRESS,
                userProgressRepository.findSortedBetweenByUser(USER_ONE.getId(), 0, USER_ONE_SORTED_PROGRESS.size(),
                        Progress.class));
        assertEquals(USER_TWO_SORTED_PROGRESS,
                userProgressRepository.findSortedBetweenByUser(USER_TWO.getId(), 0, USER_TWO_SORTED_PROGRESS.size(),
                        Progress.class));
        assertEquals(List.of(), userProgressRepository.findSortedBetweenByUser(1003L, 0, 100, Progress.class));

        assertEquals(USER_ONE_SORTED_PROGRESS_REVERSED,
                userProgressRepository.findLastSortedReversedByUser(USER_ONE.getId(), USER_ONE_SORTED_PROGRESS_REVERSED.size(), Progress.class));
        assertEquals(USER_ONE_SORTED_PROGRESS_REVERSED.subList(0, 1),
                userProgressRepository.findLastSortedReversedByUser(USER_ONE.getId(), 1, Progress.class));
        assertEquals(List.of(), userProgressRepository.findLastSortedReversedByUser(USER_ONE.getId(), 0, Progress.class));
        assertEquals(USER_TWO_SORTED_PROGRESS_REVERSED,
                userProgressRepository.findLastSortedReversedByUser(USER_TWO.getId(), USER_TWO_SORTED_PROGRESS_REVERSED.size(), Progress.class));
        assertEquals(List.of(), userProgressRepository.findLastSortedReversedByUser(1003L, 100, Progress.class));
    }
}
