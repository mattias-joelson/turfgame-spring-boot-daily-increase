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
    private static final UserProgressType TYPE = UserProgressType.DAILY_INCREASE;
    private static final Instant DATE = TIME.truncatedTo(ChronoUnit.DAYS);
    private static final int PREVIOUS_DAY_COMPLETED = 5;
    private static final int DAY_COMPLETED = 1;

    @Test
    public void testUser() {
        assertThrows(NullPointerException.class,
                () -> new UserProgress(null, TYPE, DATE, PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, TIME));

        User user = new User(USER.getId() * 2, USER.getName() + "Name", TIME.plusSeconds(3));
        assertNotEquals(USER, user);
        UserProgress userProgress = new UserProgress(user, TYPE, DATE, PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, TIME);
        assertEquals(user, userProgress.getUser());
    }

    @Test
    public void testType() {
        assertThrows(NullPointerException.class,
                () -> new UserProgress(USER, null, DATE, PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, TIME));

        UserProgressType type = UserProgressType.DAILY_ADD;
        assertNotEquals(type, TYPE);
        UserProgress userProgress = new UserProgress(USER, type, DATE, PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, TIME);
        assertEquals(type, userProgress.getType());
    }

    @Test
    public void testDate() {
        assertThrows(NullPointerException.class,
                () -> new UserProgress(USER, TYPE, null, PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, TIME));
        assertThrows(IllegalArgumentException.class,
                () -> new UserProgress(USER, TYPE, DATE.plusSeconds(3), PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, TIME));

        Instant date = DATE.plus(3, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        UserProgress userProgress = new UserProgress(USER, TYPE, date, PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, TIME);
        assertEquals(date, userProgress.getDate());
    }

    @Test
    public void testPreviousDayCompleted() {
        assertThrows(NullPointerException.class, () -> new UserProgress(USER, TYPE, DATE, null, DAY_COMPLETED, TIME));
        assertThrows(IllegalArgumentException.class, () -> new UserProgress(USER, TYPE, DATE, -1, DAY_COMPLETED, TIME));
        assertDoesNotThrow(() -> new UserProgress(USER, TYPE, DATE, 0, DAY_COMPLETED, TIME));

        Integer previousDayCompleted = PREVIOUS_DAY_COMPLETED + 3;
        assertNotEquals(PREVIOUS_DAY_COMPLETED, previousDayCompleted);
        UserProgress userProgress = new UserProgress(USER, TYPE, DATE, previousDayCompleted, DAY_COMPLETED, TIME);
        assertEquals(previousDayCompleted, userProgress.getPreviousDayCompleted());
    }

    @Test
    public void testDayCompleted() {
        assertThrows(NullPointerException.class,
                () -> new UserProgress(USER, TYPE, DATE, PREVIOUS_DAY_COMPLETED, null, TIME));
        assertThrows(IllegalArgumentException.class,
                () -> new UserProgress(USER, TYPE, DATE, PREVIOUS_DAY_COMPLETED, 0, TIME));
        assertThrows(IllegalArgumentException.class,
                () -> new UserProgress(USER, TYPE, DATE, PREVIOUS_DAY_COMPLETED, -1, TIME));

        Integer dayCompleted = DAY_COMPLETED + 3;
        assertNotEquals(DAY_COMPLETED, dayCompleted);
        UserProgress userProgress = new UserProgress(USER, TYPE, DATE, PREVIOUS_DAY_COMPLETED, dayCompleted, TIME);
        assertEquals(dayCompleted, userProgress.getDayCompleted());

        Integer newDayCompleted = dayCompleted + 1;
        assertNotEquals(DAY_COMPLETED, newDayCompleted);
        assertNotEquals(dayCompleted, newDayCompleted);
        userProgress.setDayCompleted(newDayCompleted);
        assertEquals(newDayCompleted, userProgress.getDayCompleted());

        assertThrows(NullPointerException.class, () -> userProgress.setDayCompleted(null));
        assertThrows(IllegalArgumentException.class, () -> userProgress.setDayCompleted(0));
        assertThrows(IllegalArgumentException.class, () -> userProgress.setDayCompleted(dayCompleted));
    }

    @Test
    public void testTimeCompleted() {
        assertThrows(NullPointerException.class,
                () -> new UserProgress(USER, TYPE, DATE, PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, null));
        assertThrows(IllegalArgumentException.class,
                () -> new UserProgress(USER, TYPE, DATE, PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, TIME.plusNanos(3)));

        Instant timeCompleted = TIME.plusSeconds(5);
        assertNotEquals(TIME, timeCompleted);
        UserProgress userProgress = new UserProgress(USER, TYPE, DATE, PREVIOUS_DAY_COMPLETED, DAY_COMPLETED,
                timeCompleted);
        assertEquals(timeCompleted, userProgress.getTimeCompleted());

        Instant newTimeCompleted = timeCompleted.plusSeconds(5);
        assertNotEquals(TIME, newTimeCompleted);
        assertNotEquals(timeCompleted, newTimeCompleted);
        userProgress.setTimeCompleted(newTimeCompleted);
        assertEquals(newTimeCompleted, userProgress.getTimeCompleted());

        assertThrows(NullPointerException.class, () -> userProgress.setTimeCompleted(null));
        assertThrows(IllegalArgumentException.class,
                () -> userProgress.setTimeCompleted(newTimeCompleted.plusNanos(4)));
        assertThrows(IllegalArgumentException.class, () -> userProgress.setTimeCompleted(timeCompleted));
    }

    @Test
    public void testEquals() {
        UserProgress userProgress = new UserProgress(USER, TYPE, DATE, PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, TIME);
        assertEquals(userProgress, userProgress);
        assertNotEquals(userProgress, null);
        assertNotEquals(userProgress, new UserProgress());

        User user = new User(USER.getId() * 2, USER.getName() + "Name", TIME.plusSeconds(3));
        assertNotEquals(USER, user);
        assertNotEquals(userProgress, new UserProgress(user, TYPE, DATE, PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, TIME));

        UserProgressType type = UserProgressType.DAILY_ADD;
        assertNotEquals(type, TYPE);
        assertNotEquals(userProgress, new UserProgress(USER, type, DATE, PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, TIME));

        Instant date = DATE.plus(3, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        assertNotEquals(userProgress, new UserProgress(USER, TYPE, date, PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, TIME));

        Integer previousDayCompleted = PREVIOUS_DAY_COMPLETED + 3;
        assertNotEquals(PREVIOUS_DAY_COMPLETED, previousDayCompleted);
        assertNotEquals(userProgress, new UserProgress(USER, TYPE, DATE, previousDayCompleted, DAY_COMPLETED, TIME));

        Integer dayCompleted = DAY_COMPLETED + 3;
        assertNotEquals(DAY_COMPLETED, dayCompleted);
        assertNotEquals(userProgress, new UserProgress(USER, TYPE, DATE, PREVIOUS_DAY_COMPLETED, dayCompleted, TIME));

        Instant timeCompleted = TIME.plusSeconds(5);
        assertNotEquals(TIME, timeCompleted);
        assertNotEquals(userProgress,
                new UserProgress(USER, TYPE, DATE, PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, timeCompleted));
    }

    @Test
    public void testHashCode() {
        UserProgress userProgress = new UserProgress(USER, TYPE, DATE, PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, TIME);
        assertEquals(userProgress.hashCode(), userProgress.hashCode());
        assertNotEquals(userProgress.hashCode(), new UserProgress().hashCode());

        User user = new User(USER.getId() * 2, USER.getName() + "Name", TIME.plusSeconds(3));
        assertNotEquals(USER, user);
        assertNotEquals(userProgress.hashCode(),
                new UserProgress(user, TYPE, DATE, PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, TIME).hashCode());

        UserProgressType type = UserProgressType.DAILY_ADD;
        assertNotEquals(type, TYPE);
        assertNotEquals(userProgress.hashCode(),
                new UserProgress(USER, type, DATE, PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, TIME).hashCode());

        Instant date = DATE.plus(3, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        assertNotEquals(userProgress.hashCode(),
                new UserProgress(USER, TYPE, date, PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, TIME).hashCode());

        Integer previousDayCompleted = PREVIOUS_DAY_COMPLETED + 3;
        assertNotEquals(PREVIOUS_DAY_COMPLETED, previousDayCompleted);
        assertEquals(userProgress.hashCode(),
                new UserProgress(USER, TYPE, DATE, previousDayCompleted, DAY_COMPLETED, TIME).hashCode());

        Integer dayCompleted = DAY_COMPLETED + 3;
        assertNotEquals(DAY_COMPLETED, dayCompleted);
        assertEquals(userProgress.hashCode(),
                new UserProgress(USER, TYPE, DATE, PREVIOUS_DAY_COMPLETED, dayCompleted, TIME).hashCode());

        Instant timeCompleted = TIME.plusSeconds(5);
        assertNotEquals(TIME, timeCompleted);
        assertEquals(userProgress.hashCode(),
                new UserProgress(USER, TYPE, DATE, PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, timeCompleted).hashCode());
    }
}
