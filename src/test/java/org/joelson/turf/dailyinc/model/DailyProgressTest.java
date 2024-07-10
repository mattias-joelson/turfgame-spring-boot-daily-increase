package org.joelson.turf.dailyinc.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DailyProgressTest {

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private static final int PREVIOUS_DAY_COMPLETED = 5;
    private static final int DAY_COMPLETED = 1;

    @Test
    public void testPrevious() {
        assertThrows(NullPointerException.class, () -> new DailyProgress(null, DAY_COMPLETED, TIME));
        assertThrows(IllegalArgumentException.class, () -> new DailyProgress(-1, DAY_COMPLETED, TIME));
        assertDoesNotThrow(() -> new DailyProgress(0, DAY_COMPLETED, TIME));

        Integer previous = PREVIOUS_DAY_COMPLETED + 3;
        assertNotEquals(PREVIOUS_DAY_COMPLETED, previous);
        DailyProgress dailyProgress = new DailyProgress(previous, DAY_COMPLETED, TIME);
        assertEquals(previous, dailyProgress.getPrevious());
    }

    @Test
    public void testCompleted() {
        assertThrows(NullPointerException.class, () -> new DailyProgress(PREVIOUS_DAY_COMPLETED, null, TIME));
        assertThrows(IllegalArgumentException.class, () -> new DailyProgress(PREVIOUS_DAY_COMPLETED, 0, TIME));
        assertThrows(IllegalArgumentException.class, () -> new DailyProgress(PREVIOUS_DAY_COMPLETED, -1, TIME));
        assertThrows(IllegalArgumentException.class, () -> new DailyProgress(PREVIOUS_DAY_COMPLETED, 7, TIME));

        Integer completed = DAY_COMPLETED + 4;
        assertNotEquals(DAY_COMPLETED, completed);
        DailyProgress dailyProgress = new DailyProgress(PREVIOUS_DAY_COMPLETED, completed, TIME);
        assertEquals(completed, dailyProgress.getCompleted());

        Integer newDayCompleted = completed + 1;
        assertNotEquals(DAY_COMPLETED, newDayCompleted);
        assertNotEquals(completed, newDayCompleted);
        dailyProgress.setCompleted(newDayCompleted);
        assertEquals(newDayCompleted, dailyProgress.getCompleted());

        assertThrows(NullPointerException.class, () -> dailyProgress.setCompleted(null));
        assertThrows(IllegalArgumentException.class, () -> dailyProgress.setCompleted(0));
        assertThrows(IllegalArgumentException.class, () -> dailyProgress.setCompleted(completed));
    }

    @Test
    public void testTime() {
        assertThrows(NullPointerException.class, () -> new DailyProgress(PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, null));
        assertThrows(IllegalArgumentException.class,
                () -> new DailyProgress(PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, TIME.plusNanos(3)));

        Instant timeCompleted = TIME.plusSeconds(5);
        assertNotEquals(TIME, timeCompleted);
        DailyProgress dailyProgress = new DailyProgress(PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, timeCompleted);
        assertEquals(timeCompleted, dailyProgress.getTime());

        Instant newTimeCompleted = timeCompleted.plusSeconds(5);
        assertNotEquals(TIME, newTimeCompleted);
        assertNotEquals(timeCompleted, newTimeCompleted);
        dailyProgress.setTime(newTimeCompleted);
        assertEquals(newTimeCompleted, dailyProgress.getTime());

        assertThrows(NullPointerException.class, () -> dailyProgress.setTime(null));
        assertThrows(IllegalArgumentException.class, () -> dailyProgress.setTime(newTimeCompleted.plusNanos(4)));
        assertThrows(IllegalArgumentException.class, () -> dailyProgress.setTime(timeCompleted));
    }

    @Test
    public void testEquals() {
        DailyProgress dailyProgress = new DailyProgress(PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, TIME);
        assertEquals(dailyProgress, dailyProgress);
        assertNotEquals(dailyProgress, null);
        assertNotEquals(dailyProgress, new DailyProgress());

        Integer previousDayCompleted = PREVIOUS_DAY_COMPLETED + 3;
        assertNotEquals(PREVIOUS_DAY_COMPLETED, previousDayCompleted);
        assertNotEquals(dailyProgress, new DailyProgress(previousDayCompleted, DAY_COMPLETED, TIME));

        Integer dayCompleted = DAY_COMPLETED + 3;
        assertNotEquals(DAY_COMPLETED, dayCompleted);
        assertNotEquals(dailyProgress, new DailyProgress(PREVIOUS_DAY_COMPLETED, dayCompleted, TIME));

        Instant timeCompleted = TIME.plusSeconds(5);
        assertNotEquals(TIME, timeCompleted);
        assertNotEquals(dailyProgress, new DailyProgress(PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, timeCompleted));
    }

    @Test
    public void testHashCode() {
        DailyProgress dailyProgress = new DailyProgress(PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, TIME);
        assertEquals(dailyProgress.hashCode(), dailyProgress.hashCode());
        assertNotEquals(dailyProgress.hashCode(), new DailyProgress().hashCode());

        Integer previousDayCompleted = PREVIOUS_DAY_COMPLETED + 3;
        assertNotEquals(PREVIOUS_DAY_COMPLETED, previousDayCompleted);
        assertNotEquals(dailyProgress.hashCode(),
                new DailyProgress(previousDayCompleted, DAY_COMPLETED, TIME).hashCode());

        Integer dayCompleted = DAY_COMPLETED + 3;
        assertNotEquals(DAY_COMPLETED, dayCompleted);
        assertNotEquals(dailyProgress.hashCode(),
                new DailyProgress(PREVIOUS_DAY_COMPLETED, dayCompleted, TIME).hashCode());

        Instant timeCompleted = TIME.plusSeconds(5);
        assertNotEquals(TIME, timeCompleted);
        assertNotEquals(dailyProgress.hashCode(),
                new DailyProgress(PREVIOUS_DAY_COMPLETED, DAY_COMPLETED, timeCompleted).hashCode());
    }
}
