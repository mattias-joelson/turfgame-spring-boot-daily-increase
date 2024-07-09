package org.joelson.turf.dailyinc.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserProgressTest {

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private static final User USER = new User(1L, "User", TIME);
    private static final Instant DATE = TIME.truncatedTo(ChronoUnit.DAYS);
    private static final int PREVIOUS_DAY_COMPLETED = 5;
    private static final int DAY_COMPLETED = 1;

    private static final UserProgressTypeProgress USER_TYPE_PROGRESS
            = new UserProgressTypeProgress(PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, TIME);

    @Test
    public void testUser() {
        assertThrows(NullPointerException.class,
                () -> new UserProgress(null, DATE, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS));

        User user = new User(USER.getId() * 2, USER.getName() + "Name", TIME.plusSeconds(3));
        assertNotEquals(USER, user);
        UserProgress userProgress = new UserProgress(user, DATE, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                USER_TYPE_PROGRESS, USER_TYPE_PROGRESS);
        assertEquals(user, userProgress.getUser());
    }

    @Test
    public void testDate() {
        assertThrows(NullPointerException.class,
                () -> new UserProgress(USER, null, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS));
        assertThrows(IllegalArgumentException.class,
                () -> new UserProgress(USER, DATE.plusSeconds(3), USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS, USER_TYPE_PROGRESS));

        Instant date = DATE.plus(3, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        UserProgress userProgress = new UserProgress(USER, date, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                USER_TYPE_PROGRESS, USER_TYPE_PROGRESS);
        assertEquals(date, userProgress.getDate());
    }

    @Test
    public void testIncrease() {
        assertThrows(NullPointerException.class,
                () -> new UserProgress(USER, DATE, null, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS));
        assertDoesNotThrow(
                () -> new UserProgress(USER, DATE, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS));

        UserProgressTypeProgress userProgressTypeProgress = new UserProgressTypeProgress(PREVIOUS_DAY_COMPLETED + 3,
                DAY_COMPLETED, TIME);
        assertNotEquals(USER_TYPE_PROGRESS, userProgressTypeProgress);
        UserProgress userProgress = new UserProgress(USER, DATE, userProgressTypeProgress, USER_TYPE_PROGRESS,
                USER_TYPE_PROGRESS, USER_TYPE_PROGRESS);
        assertEquals(userProgressTypeProgress, userProgress.getIncrease());
        userProgress = new UserProgress(USER, DATE, USER_TYPE_PROGRESS, userProgressTypeProgress, USER_TYPE_PROGRESS,
                USER_TYPE_PROGRESS);
        assertEquals(userProgressTypeProgress, userProgress.getAdd());
        userProgress = new UserProgress(USER, DATE, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, userProgressTypeProgress,
                USER_TYPE_PROGRESS);
        assertEquals(userProgressTypeProgress, userProgress.getFibonacci());
        userProgress = new UserProgress(USER, DATE, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                userProgressTypeProgress);
        assertEquals(userProgressTypeProgress, userProgress.getPowerOfTwo());
    }

    @Test
    public void testEquals() {
        UserProgress userProgress = new UserProgress(USER, DATE, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                USER_TYPE_PROGRESS, USER_TYPE_PROGRESS);
        assertEquals(userProgress, userProgress);
        assertNotEquals(userProgress, null);
        assertNotEquals(userProgress, new UserProgress());

        User user = new User(USER.getId() * 2, USER.getName() + "Name", TIME.plusSeconds(3));
        assertNotEquals(USER, user);
        assertNotEquals(userProgress,
                new UserProgress(user, DATE, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS));

        Instant date = DATE.plus(3, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        assertNotEquals(userProgress,
                new UserProgress(USER, date, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS));

        Integer previousDayCompleted = PREVIOUS_DAY_COMPLETED + 3;
        UserProgressTypeProgress userProgressTypeProgress = new UserProgressTypeProgress(PREVIOUS_DAY_COMPLETED + 3,
                DAY_COMPLETED, TIME);
        assertNotEquals(USER_TYPE_PROGRESS, previousDayCompleted);
        assertNotEquals(userProgress,
                new UserProgress(USER, DATE, userProgressTypeProgress, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS));
        assertNotEquals(userProgress,
                new UserProgress(USER, DATE, USER_TYPE_PROGRESS, userProgressTypeProgress, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS));
        assertNotEquals(userProgress,
                new UserProgress(USER, DATE, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, userProgressTypeProgress,
                        USER_TYPE_PROGRESS));
        assertNotEquals(userProgress,
                new UserProgress(USER, DATE, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        userProgressTypeProgress));
    }

    @Test
    public void testHashCode() {
        UserProgress userProgress = new UserProgress(USER, DATE, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                USER_TYPE_PROGRESS, USER_TYPE_PROGRESS);
        assertEquals(userProgress.hashCode(), userProgress.hashCode());
        assertNotEquals(userProgress.hashCode(), new UserProgress().hashCode());

        User user = new User(USER.getId() * 2, USER.getName() + "Name", TIME.plusSeconds(3));
        assertNotEquals(USER, user);
        assertNotEquals(userProgress.hashCode(),
                new UserProgress(user, DATE, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS).hashCode());

        Instant date = DATE.plus(3, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        assertNotEquals(userProgress.hashCode(),
                new UserProgress(USER, date, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS).hashCode());

        Integer previousDayCompleted = PREVIOUS_DAY_COMPLETED + 3;
        UserProgressTypeProgress userProgressTypeProgress = new UserProgressTypeProgress(PREVIOUS_DAY_COMPLETED + 3,
                DAY_COMPLETED, TIME);
        assertNotEquals(USER_TYPE_PROGRESS, previousDayCompleted);
        assertEquals(userProgress.hashCode(),
                new UserProgress(USER, DATE, userProgressTypeProgress, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS).hashCode());
        assertEquals(userProgress.hashCode(),
                new UserProgress(USER, DATE, USER_TYPE_PROGRESS, userProgressTypeProgress, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS).hashCode());
        assertEquals(userProgress.hashCode(),
                new UserProgress(USER, DATE, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, userProgressTypeProgress,
                        USER_TYPE_PROGRESS).hashCode());
        assertEquals(userProgress.hashCode(),
                new UserProgress(USER, DATE, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        userProgressTypeProgress).hashCode());
    }
}
