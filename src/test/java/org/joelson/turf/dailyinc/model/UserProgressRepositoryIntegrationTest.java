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

    private static final User USER_ONE = new User(1L, "UserOne", NEXT_TIME);
    private static final UserProgress USER_ONE_INC_PROGRESS = new UserProgress(USER_ONE,
            UserProgressType.DAILY_INCREASE, DATE, 0, 1, TIME);
    private static final UserProgress USER_ONE_NEXT_PROGRESS = new UserProgress(USER_ONE,
            UserProgressType.DAILY_INCREASE, NEXT_DATE, 1, 2, NEXT_TIME);
    private static final UserProgress USER_ONE_ADD_PROGRESS = new UserProgress(USER_ONE, UserProgressType.DAILY_ADD,
            DATE, 0, 1, TIME);

    private static final User USER_TWO = new User(2L, "UserTwo", NEXT_TIME);
    private static final UserProgress USER_TWO_INC_PROGRESS = new UserProgress(USER_TWO,
            UserProgressType.DAILY_INCREASE, DATE, 10, 10, TIME);
    private static final UserProgress USER_TWO_NEXT_PROGRESS = new UserProgress(USER_TWO,
            UserProgressType.DAILY_INCREASE, NEXT_DATE, 11, 12, TIME);

    public static final List<UserProgress> SORTED_USER_PROGRESS = List.of(USER_ONE_INC_PROGRESS, USER_ONE_NEXT_PROGRESS,
            USER_ONE_ADD_PROGRESS, USER_TWO_INC_PROGRESS, USER_TWO_NEXT_PROGRESS);

    public static final List<UserProgress> USER_ONE_SORTED_USER_PROGRESS = List.of(USER_ONE_INC_PROGRESS,
            USER_ONE_NEXT_PROGRESS, USER_ONE_ADD_PROGRESS);
    public static final List<UserProgress> USER_TWO_SORTED_USER_PROGRESS = List.of(USER_TWO_INC_PROGRESS,
            USER_TWO_NEXT_PROGRESS);

    @Autowired
    UserProgressRepository userProgressRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void withUserProgress_whenFindById_thenSuccess() {
        entityManager.persist(USER_ONE);
        entityManager.persist(USER_TWO);
        entityManager.persist(USER_ONE_INC_PROGRESS);
        entityManager.persist(USER_ONE_ADD_PROGRESS);
        entityManager.persist(USER_ONE_NEXT_PROGRESS);
        entityManager.persist(USER_TWO_INC_PROGRESS);
        entityManager.persist(USER_TWO_NEXT_PROGRESS);

        UserProgress userProgress = userProgressRepository.findById(
                new UserProgressId(USER_ONE_INC_PROGRESS.getUser().getId(), USER_ONE_INC_PROGRESS.getType(),
                        USER_ONE_INC_PROGRESS.getDate())).orElse(null);
        assertEquals(USER_ONE_INC_PROGRESS, userProgress);
        assertEquals(USER_ONE_ADD_PROGRESS, userProgressRepository.findById(
                new UserProgressId(USER_ONE.getId(), UserProgressType.DAILY_ADD, DATE)).orElse(null));
        assertEquals(USER_ONE_NEXT_PROGRESS, userProgressRepository.findById(
                new UserProgressId(USER_ONE.getId(), UserProgressType.DAILY_INCREASE, NEXT_DATE)).orElse(null));
        assertEquals(USER_TWO_INC_PROGRESS, userProgressRepository.findById(
                new UserProgressId(USER_TWO.getId(), UserProgressType.DAILY_INCREASE, DATE)).orElse(null));
        assertEquals(USER_TWO_NEXT_PROGRESS, userProgressRepository.findById(
                new UserProgressId(USER_TWO.getId(), UserProgressType.DAILY_INCREASE, NEXT_DATE)).orElse(null));

        assertNull(userProgressRepository.findById(new UserProgressId(1000L, USER_ONE_INC_PROGRESS.getType(),
                        USER_ONE_INC_PROGRESS.getDate())).orElse(null));
        assertNull(userProgressRepository.findById(
                new UserProgressId(USER_ONE.getId(), UserProgressType.DAILY_FIBONACCI, DATE)).orElse(null));
        assertNull(userProgressRepository.findById(new UserProgressId(USER_ONE.getId(), USER_ONE_INC_PROGRESS.getType(),
                NEXT_DATE.plus(1, ChronoUnit.DAYS))).orElse(null));
    }

    @Test
    public void givenNewUserProgress_whenSave_thenSuccess() {
        entityManager.persist(USER_ONE);

        UserProgress savedUserProgress = userProgressRepository.save(USER_ONE_INC_PROGRESS);
        assertEquals(USER_ONE_INC_PROGRESS, entityManager.find(UserProgress.class,
                new UserProgressId(savedUserProgress.getUser().getId(), savedUserProgress.getType(),
                        savedUserProgress.getDate())));

        assertThrows(EntityExistsException.class, () -> entityManager.persist(USER_ONE_INC_PROGRESS));
    }

    @Test
    public void givenUserProgress_whenUpdate_thenSuccess() {
        UserProgress userProgress = new UserProgress(USER_TWO, UserProgressType.DAILY_POWER_OF_TWO, DATE, 5, 4, TIME);
        entityManager.persist(userProgress);

        userProgress.setDayCompleted(userProgress.getDayCompleted() + 1);
        userProgress.setTimeCompleted(TIME.plusSeconds(60));
        UserProgress savedUserProgress = userProgressRepository.save(userProgress);
        assertEquals(userProgress, entityManager.find(UserProgress.class,
                new UserProgressId(savedUserProgress.getUser().getId(), savedUserProgress.getType(),
                        savedUserProgress.getDate())));
    }

    @Test
    public void givenUserProgress_whenFindAllSorted_thenSuccess() {
        entityManager.persist(USER_ONE_ADD_PROGRESS);
        entityManager.persist(USER_TWO_NEXT_PROGRESS);
        entityManager.persist(USER_TWO_INC_PROGRESS);
        entityManager.persist(USER_ONE_NEXT_PROGRESS);
        entityManager.persist(USER_ONE_INC_PROGRESS);

        List<UserProgress> userProgresses = userProgressRepository.findAllSorted(UserProgress.class);
        assertEquals(SORTED_USER_PROGRESS, userProgresses);
    }

    @Test
    public void givenUserProgress_whenFindAllSortedByUser_thenSuccess() {
        entityManager.persist(USER_ONE_ADD_PROGRESS);
        entityManager.persist(USER_TWO_NEXT_PROGRESS);
        entityManager.persist(USER_TWO_INC_PROGRESS);
        entityManager.persist(USER_ONE_NEXT_PROGRESS);
        entityManager.persist(USER_ONE_INC_PROGRESS);

        List<UserProgress> userOneProgresses = userProgressRepository.findAllSortedByUser(USER_ONE.getId(),
                UserProgress.class);
        assertEquals(USER_ONE_SORTED_USER_PROGRESS, userOneProgresses);
        List<UserProgress> userTwoProgresses = userProgressRepository.findAllSortedByUser(USER_TWO.getId(),
                UserProgress.class);
        assertEquals(USER_TWO_SORTED_USER_PROGRESS, userTwoProgresses);
    }
}
