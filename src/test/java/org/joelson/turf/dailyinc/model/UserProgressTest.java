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
    private static final Integer VISITS = 1;
    private static final int PREVIOUS_DAY_COMPLETED = 5;
    private static final int DAY_COMPLETED = 1;

    private static final DailyProgress USER_TYPE_PROGRESS = new DailyProgress(PREVIOUS_DAY_COMPLETED, DAY_COMPLETED,
            TIME);

    @Test
    public void testUser() {
        assertThrows(NullPointerException.class,
                () -> new UserProgress(null, DATE, VISITS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS));

        User user = new User(USER.getId() * 2, USER.getName() + "Name", TIME.plusSeconds(3));
        assertNotEquals(USER, user);
        UserProgress userProgress = new UserProgress(user, DATE, VISITS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                USER_TYPE_PROGRESS, USER_TYPE_PROGRESS);
        assertEquals(user, userProgress.getUser());
    }

    @Test
    public void testDate() {
        assertThrows(NullPointerException.class,
                () -> new UserProgress(USER, null, VISITS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS));
        assertThrows(IllegalArgumentException.class,
                () -> new UserProgress(USER, DATE.plusSeconds(3), VISITS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS, USER_TYPE_PROGRESS));

        Instant date = DATE.plus(3, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        UserProgress userProgress = new UserProgress(USER, date, VISITS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                USER_TYPE_PROGRESS, USER_TYPE_PROGRESS);
        assertEquals(date, userProgress.getDate());
    }

    @Test
    public void testVisits() {
        assertThrows(NullPointerException.class, () -> new UserProgress(USER, DATE, null, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS));
        assertThrows(IllegalArgumentException.class, () -> new UserProgress(USER, DATE, 0, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS));
        assertThrows(IllegalArgumentException.class, () -> new UserProgress(USER, DATE, -1, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS));

        Integer visits = VISITS + 3;
        assertNotEquals(VISITS, visits);
        UserProgress userProgress = new UserProgress(USER, DATE, visits, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS);
        assertEquals(visits, userProgress.getVisits());

        Integer newVisits = visits + 1;
        assertNotEquals(VISITS, newVisits);
        assertNotEquals(visits, newVisits);
        userProgress.setVisits(newVisits);
        assertEquals(newVisits, userProgress.getVisits());

        assertThrows(NullPointerException.class, () -> userProgress.setVisits(null));
        assertThrows(IllegalArgumentException.class, () -> userProgress.setVisits(0));
        assertThrows(IllegalArgumentException.class, () -> userProgress.setVisits(visits));
        assertDoesNotThrow(() -> userProgress.setVisits(newVisits));
    }

    @Test
    public void testIncrease() {
        assertThrows(NullPointerException.class,
                () -> new UserProgress(USER, DATE, VISITS, null, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS));
        assertDoesNotThrow(
                () -> new UserProgress(USER, DATE, VISITS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS));

        DailyProgress dailyProgress = new DailyProgress(PREVIOUS_DAY_COMPLETED + 3, DAY_COMPLETED, TIME);
        assertNotEquals(USER_TYPE_PROGRESS, dailyProgress);
        UserProgress userProgress = new UserProgress(USER, DATE, VISITS, dailyProgress, USER_TYPE_PROGRESS,
                USER_TYPE_PROGRESS, USER_TYPE_PROGRESS);
        assertEquals(dailyProgress, userProgress.getIncrease());
        userProgress = new UserProgress(USER, DATE, VISITS, USER_TYPE_PROGRESS, dailyProgress, USER_TYPE_PROGRESS,
                USER_TYPE_PROGRESS);
        assertEquals(dailyProgress, userProgress.getAdd());
        userProgress = new UserProgress(USER, DATE, VISITS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, dailyProgress,
                USER_TYPE_PROGRESS);
        assertEquals(dailyProgress, userProgress.getFibonacci());
        userProgress = new UserProgress(USER, DATE, VISITS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                dailyProgress);
        assertEquals(dailyProgress, userProgress.getPowerOfTwo());
    }

    @Test
    public void testEquals() {
        UserProgress userProgress = new UserProgress(USER, DATE, VISITS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                USER_TYPE_PROGRESS, USER_TYPE_PROGRESS);
        assertEquals(userProgress, userProgress);
        assertNotEquals(userProgress, null);
        assertNotEquals(userProgress, new UserProgress());

        User user = new User(USER.getId() * 2, USER.getName() + "Name", TIME.plusSeconds(3));
        assertNotEquals(USER, user);
        assertNotEquals(userProgress,
                new UserProgress(user, DATE, VISITS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS));

        Instant date = DATE.plus(3, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        assertNotEquals(userProgress,
                new UserProgress(USER, date, VISITS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS));

        Integer visits = VISITS + 2;
        assertNotEquals(VISITS, visits);
        assertNotEquals(userProgress,
                new UserProgress(USER, DATE, visits, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS));

        Integer previousDayCompleted = PREVIOUS_DAY_COMPLETED + 3;
        DailyProgress dailyProgress = new DailyProgress(PREVIOUS_DAY_COMPLETED + 3, DAY_COMPLETED, TIME);
        assertNotEquals(USER_TYPE_PROGRESS, previousDayCompleted);
        assertNotEquals(userProgress,
                new UserProgress(USER, DATE, VISITS, dailyProgress, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS));
        assertNotEquals(userProgress,
                new UserProgress(USER, DATE, VISITS, USER_TYPE_PROGRESS, dailyProgress, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS));
        assertNotEquals(userProgress,
                new UserProgress(USER, DATE, VISITS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, dailyProgress,
                        USER_TYPE_PROGRESS));
        assertNotEquals(userProgress,
                new UserProgress(USER, DATE, VISITS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        dailyProgress));
    }

    @Test
    public void testHashCode() {
        UserProgress userProgress = new UserProgress(USER, DATE, VISITS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                USER_TYPE_PROGRESS, USER_TYPE_PROGRESS);
        assertEquals(userProgress.hashCode(), userProgress.hashCode());
        assertNotEquals(userProgress.hashCode(), new UserProgress().hashCode());

        User user = new User(USER.getId() * 2, USER.getName() + "Name", TIME.plusSeconds(3));
        assertNotEquals(USER, user);
        assertNotEquals(userProgress.hashCode(),
                new UserProgress(user, DATE, VISITS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS).hashCode());

        Instant date = DATE.plus(3, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        assertNotEquals(userProgress.hashCode(),
                new UserProgress(USER, date, VISITS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS).hashCode());

        Integer visits = VISITS + 2;
        assertNotEquals(VISITS.hashCode(), visits.hashCode());
        assertEquals(userProgress.hashCode(),
                new UserProgress(USER, DATE, visits, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS).hashCode());

        Integer previousDayCompleted = PREVIOUS_DAY_COMPLETED + 3;
        DailyProgress dailyProgress = new DailyProgress(PREVIOUS_DAY_COMPLETED + 3, DAY_COMPLETED, TIME);
        assertNotEquals(USER_TYPE_PROGRESS, previousDayCompleted);
        assertEquals(userProgress.hashCode(),
                new UserProgress(USER, DATE, VISITS, dailyProgress, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS).hashCode());
        assertEquals(userProgress.hashCode(),
                new UserProgress(USER, DATE, VISITS, USER_TYPE_PROGRESS, dailyProgress, USER_TYPE_PROGRESS,
                        USER_TYPE_PROGRESS).hashCode());
        assertEquals(userProgress.hashCode(),
                new UserProgress(USER, DATE, VISITS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, dailyProgress,
                        USER_TYPE_PROGRESS).hashCode());
        assertEquals(userProgress.hashCode(),
                new UserProgress(USER, DATE, VISITS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS, USER_TYPE_PROGRESS,
                        dailyProgress).hashCode());
    }
}
