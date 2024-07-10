package org.joelson.turf.dailyinc.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProgressTest {

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private static final User USER = new User(1L, "User", TIME);
    private static final Instant DATE = TIME.truncatedTo(ChronoUnit.DAYS);
    private static final Integer VISITS = 1;
    private static final int PREVIOUS_DAY_COMPLETED = 5;
    private static final int DAY_COMPLETED = 1;

    private static final DailyProgress DAILY_PROGRESS = new DailyProgress(PREVIOUS_DAY_COMPLETED, DAY_COMPLETED,
            TIME);

    @Test
    public void testUser() {
        assertThrows(NullPointerException.class,
                () -> new Progress(null, DATE, VISITS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS));

        User user = new User(USER.getId() * 2, USER.getName() + "Name", TIME.plusSeconds(3));
        assertNotEquals(USER, user);
        Progress progress = new Progress(user, DATE, VISITS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS,
                DAILY_PROGRESS);
        assertEquals(user, progress.getUser());
    }

    @Test
    public void testDate() {
        assertThrows(NullPointerException.class,
                () -> new Progress(USER, null, VISITS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS));
        assertThrows(IllegalArgumentException.class,
                () -> new Progress(USER, DATE.plusSeconds(3), VISITS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS,
                        DAILY_PROGRESS));

        Instant date = DATE.plus(3, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        Progress progress = new Progress(USER, date, VISITS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS,
                DAILY_PROGRESS);
        assertEquals(date, progress.getDate());
    }

    @Test
    public void testVisits() {
        assertThrows(NullPointerException.class,
                () -> new Progress(USER, DATE, null, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS));
        assertThrows(IllegalArgumentException.class,
                () -> new Progress(USER, DATE, 0, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS));
        assertThrows(IllegalArgumentException.class,
                () -> new Progress(USER, DATE, -1, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS));

        Integer visits = VISITS + 3;
        assertNotEquals(VISITS, visits);
        Progress progress = new Progress(USER, DATE, visits, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS,
                DAILY_PROGRESS);
        assertEquals(visits, progress.getVisits());

        Integer newVisits = visits + 1;
        assertNotEquals(VISITS, newVisits);
        assertNotEquals(visits, newVisits);
        progress.setVisits(newVisits);
        assertEquals(newVisits, progress.getVisits());

        assertThrows(NullPointerException.class, () -> progress.setVisits(null));
        assertThrows(IllegalArgumentException.class, () -> progress.setVisits(0));
        assertThrows(IllegalArgumentException.class, () -> progress.setVisits(visits));
        assertDoesNotThrow(() -> progress.setVisits(newVisits));
    }

    @Test
    public void testIncrease() {
        assertThrows(NullPointerException.class,
                () -> new Progress(USER, DATE, VISITS, null, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS));
        assertDoesNotThrow(
                () -> new Progress(USER, DATE, VISITS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS));

        DailyProgress dailyProgress = new DailyProgress(PREVIOUS_DAY_COMPLETED + 3, DAY_COMPLETED, TIME);
        assertNotEquals(DAILY_PROGRESS, dailyProgress);
        Progress progress = new Progress(USER, DATE, VISITS, dailyProgress, DAILY_PROGRESS, DAILY_PROGRESS,
                DAILY_PROGRESS);
        assertEquals(dailyProgress, progress.getIncrease());
        progress = new Progress(USER, DATE, VISITS, DAILY_PROGRESS, dailyProgress, DAILY_PROGRESS, DAILY_PROGRESS);
        assertEquals(dailyProgress, progress.getAdd());
        progress = new Progress(USER, DATE, VISITS, DAILY_PROGRESS, DAILY_PROGRESS, dailyProgress, DAILY_PROGRESS);
        assertEquals(dailyProgress, progress.getFibonacci());
        progress = new Progress(USER, DATE, VISITS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS, dailyProgress);
        assertEquals(dailyProgress, progress.getPowerOfTwo());
    }

    @Test
    public void testEquals() {
        Progress progress = new Progress(USER, DATE, VISITS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS,
                DAILY_PROGRESS);
        assertEquals(progress, progress);
        assertNotEquals(progress, null);
        assertNotEquals(progress, new Progress());

        User user = new User(USER.getId() * 2, USER.getName() + "Name", TIME.plusSeconds(3));
        assertNotEquals(USER, user);
        assertNotEquals(progress,
                new Progress(user, DATE, VISITS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS));

        Instant date = DATE.plus(3, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        assertNotEquals(progress,
                new Progress(USER, date, VISITS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS));

        Integer visits = VISITS + 2;
        assertNotEquals(VISITS, visits);
        assertNotEquals(progress,
                new Progress(USER, DATE, visits, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS));

        Integer previousDayCompleted = PREVIOUS_DAY_COMPLETED + 3;
        DailyProgress dailyProgress = new DailyProgress(PREVIOUS_DAY_COMPLETED + 3, DAY_COMPLETED, TIME);
        assertNotEquals(DAILY_PROGRESS, previousDayCompleted);
        assertNotEquals(progress,
                new Progress(USER, DATE, VISITS, dailyProgress, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS));
        assertNotEquals(progress,
                new Progress(USER, DATE, VISITS, DAILY_PROGRESS, dailyProgress, DAILY_PROGRESS, DAILY_PROGRESS));
        assertNotEquals(progress,
                new Progress(USER, DATE, VISITS, DAILY_PROGRESS, DAILY_PROGRESS, dailyProgress, DAILY_PROGRESS));
        assertNotEquals(progress,
                new Progress(USER, DATE, VISITS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS, dailyProgress));
    }

    @Test
    public void testHashCode() {
        Progress progress = new Progress(USER, DATE, VISITS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS,
                DAILY_PROGRESS);
        assertEquals(progress.hashCode(), progress.hashCode());
        assertNotEquals(progress.hashCode(), new Progress().hashCode());

        User user = new User(USER.getId() * 2, USER.getName() + "Name", TIME.plusSeconds(3));
        assertNotEquals(USER, user);
        assertNotEquals(progress.hashCode(),
                new Progress(user, DATE, VISITS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS,
                        DAILY_PROGRESS).hashCode());

        Instant date = DATE.plus(3, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        assertNotEquals(progress.hashCode(),
                new Progress(USER, date, VISITS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS,
                        DAILY_PROGRESS).hashCode());

        Integer visits = VISITS + 2;
        assertNotEquals(VISITS.hashCode(), visits.hashCode());
        assertEquals(progress.hashCode(),
                new Progress(USER, DATE, visits, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS,
                        DAILY_PROGRESS).hashCode());

        Integer previousDayCompleted = PREVIOUS_DAY_COMPLETED + 3;
        DailyProgress dailyProgress = new DailyProgress(PREVIOUS_DAY_COMPLETED + 3, DAY_COMPLETED, TIME);
        assertNotEquals(DAILY_PROGRESS, previousDayCompleted);
        assertEquals(progress.hashCode(),
                new Progress(USER, DATE, VISITS, dailyProgress, DAILY_PROGRESS, DAILY_PROGRESS,
                        DAILY_PROGRESS).hashCode());
        assertEquals(progress.hashCode(),
                new Progress(USER, DATE, VISITS, DAILY_PROGRESS, dailyProgress, DAILY_PROGRESS,
                        DAILY_PROGRESS).hashCode());
        assertEquals(progress.hashCode(),
                new Progress(USER, DATE, VISITS, DAILY_PROGRESS, DAILY_PROGRESS, dailyProgress,
                        DAILY_PROGRESS).hashCode());
        assertEquals(progress.hashCode(),
                new Progress(USER, DATE, VISITS, DAILY_PROGRESS, DAILY_PROGRESS, DAILY_PROGRESS,
                        dailyProgress).hashCode());
    }
}
