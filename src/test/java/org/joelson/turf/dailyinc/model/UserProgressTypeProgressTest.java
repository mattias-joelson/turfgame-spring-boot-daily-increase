package org.joelson.turf.dailyinc.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserProgressTypeProgressTest {

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private static final int PREVIOUS_DAY_COMPLETED = 5;
    private static final int DAY_COMPLETED = 1;

    @Test
    public void testPrevious() {
        assertThrows(NullPointerException.class, () -> new UserProgressTypeProgress(null, DAY_COMPLETED, TIME));
        assertThrows(IllegalArgumentException.class, () -> new UserProgressTypeProgress(-1, DAY_COMPLETED, TIME));
        assertDoesNotThrow(() -> new UserProgressTypeProgress(0, DAY_COMPLETED, TIME));

        Integer previous = PREVIOUS_DAY_COMPLETED + 3;
        assertNotEquals(PREVIOUS_DAY_COMPLETED, previous);
        UserProgressTypeProgress userProgressTypeProgress = new UserProgressTypeProgress(previous, DAY_COMPLETED, TIME);
        assertEquals(previous, userProgressTypeProgress.getPrevious());
    }

    @Test
    public void testCompleted() {
        assertThrows(NullPointerException.class,
                () -> new UserProgressTypeProgress(PREVIOUS_DAY_COMPLETED, null, TIME));
        assertThrows(IllegalArgumentException.class,
                () -> new UserProgressTypeProgress(PREVIOUS_DAY_COMPLETED, 0, TIME));
        assertThrows(IllegalArgumentException.class,
                () -> new UserProgressTypeProgress(PREVIOUS_DAY_COMPLETED, -1, TIME));
        assertThrows(IllegalArgumentException.class,
                () -> new UserProgressTypeProgress(PREVIOUS_DAY_COMPLETED, 7, TIME));

        Integer completed = DAY_COMPLETED + 4;
        assertNotEquals(DAY_COMPLETED, completed);
        UserProgressTypeProgress userProgressTypeProgress = new UserProgressTypeProgress(PREVIOUS_DAY_COMPLETED,
                completed, TIME);
        assertEquals(completed, userProgressTypeProgress.getCompleted());

        Integer newDayCompleted = completed + 1;
        assertNotEquals(DAY_COMPLETED, newDayCompleted);
        assertNotEquals(completed, newDayCompleted);
        userProgressTypeProgress.setCompleted(newDayCompleted);
        assertEquals(newDayCompleted, userProgressTypeProgress.getCompleted());

        assertThrows(NullPointerException.class, () -> userProgressTypeProgress.setCompleted(null));
        assertThrows(IllegalArgumentException.class, () -> userProgressTypeProgress.setCompleted(0));
        assertThrows(IllegalArgumentException.class, () -> userProgressTypeProgress.setCompleted(completed));
    }

    @Test
    public void testTime() {
        assertThrows(NullPointerException.class,
                () -> new UserProgressTypeProgress(PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, null));
        assertThrows(IllegalArgumentException.class,
                () -> new UserProgressTypeProgress(PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, TIME.plusNanos(3)));

        Instant timeCompleted = TIME.plusSeconds(5);
        assertNotEquals(TIME, timeCompleted);
        UserProgressTypeProgress userProgressTypeProgress = new UserProgressTypeProgress(PREVIOUS_DAY_COMPLETED,
                DAY_COMPLETED, timeCompleted);
        assertEquals(timeCompleted, userProgressTypeProgress.getTime());

        Instant newTimeCompleted = timeCompleted.plusSeconds(5);
        assertNotEquals(TIME, newTimeCompleted);
        assertNotEquals(timeCompleted, newTimeCompleted);
        userProgressTypeProgress.setTime(newTimeCompleted);
        assertEquals(newTimeCompleted, userProgressTypeProgress.getTime());

        assertThrows(NullPointerException.class, () -> userProgressTypeProgress.setTime(null));
        assertThrows(IllegalArgumentException.class,
                () -> userProgressTypeProgress.setTime(newTimeCompleted.plusNanos(4)));
        assertThrows(IllegalArgumentException.class, () -> userProgressTypeProgress.setTime(timeCompleted));
    }

    @Test
    public void testEquals() {
        UserProgressTypeProgress userProgressTypeProgress = new UserProgressTypeProgress(PREVIOUS_DAY_COMPLETED,
                DAY_COMPLETED, TIME);
        assertEquals(userProgressTypeProgress, userProgressTypeProgress);
        assertNotEquals(userProgressTypeProgress, null);
        assertNotEquals(userProgressTypeProgress, new UserProgressTypeProgress());

        Integer previousDayCompleted = PREVIOUS_DAY_COMPLETED + 3;
        assertNotEquals(PREVIOUS_DAY_COMPLETED, previousDayCompleted);
        assertNotEquals(userProgressTypeProgress,
                new UserProgressTypeProgress(previousDayCompleted, DAY_COMPLETED, TIME));

        Integer dayCompleted = DAY_COMPLETED + 3;
        assertNotEquals(DAY_COMPLETED, dayCompleted);
        assertNotEquals(userProgressTypeProgress,
                new UserProgressTypeProgress(PREVIOUS_DAY_COMPLETED, dayCompleted, TIME));

        Instant timeCompleted = TIME.plusSeconds(5);
        assertNotEquals(TIME, timeCompleted);
        assertNotEquals(userProgressTypeProgress,
                new UserProgressTypeProgress(PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, timeCompleted));
    }

    @Test
    public void testHashCode() {
        UserProgressTypeProgress userProgressTypeProgress = new UserProgressTypeProgress(PREVIOUS_DAY_COMPLETED,
                DAY_COMPLETED, TIME);
        assertEquals(userProgressTypeProgress.hashCode(), userProgressTypeProgress.hashCode());
        assertNotEquals(userProgressTypeProgress.hashCode(), new UserProgressTypeProgress().hashCode());

        Integer previousDayCompleted = PREVIOUS_DAY_COMPLETED + 3;
        assertNotEquals(PREVIOUS_DAY_COMPLETED, previousDayCompleted);
        assertNotEquals(userProgressTypeProgress.hashCode(),
                new UserProgressTypeProgress(previousDayCompleted, DAY_COMPLETED, TIME).hashCode());

        Integer dayCompleted = DAY_COMPLETED + 3;
        assertNotEquals(DAY_COMPLETED, dayCompleted);
        assertNotEquals(userProgressTypeProgress.hashCode(),
                new UserProgressTypeProgress(PREVIOUS_DAY_COMPLETED, dayCompleted, TIME).hashCode());

        Instant timeCompleted = TIME.plusSeconds(5);
        assertNotEquals(TIME, timeCompleted);
        assertNotEquals(userProgressTypeProgress.hashCode(),
                new UserProgressTypeProgress(PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, timeCompleted).hashCode());
    }
}
