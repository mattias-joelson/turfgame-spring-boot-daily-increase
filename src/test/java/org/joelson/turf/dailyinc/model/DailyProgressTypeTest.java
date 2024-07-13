package org.joelson.turf.dailyinc.model;

import org.junit.jupiter.api.Test;

import static org.joelson.turf.dailyinc.model.DailyProgressType.DAILY_ADD;
import static org.joelson.turf.dailyinc.model.DailyProgressType.DAILY_FIBONACCI;
import static org.joelson.turf.dailyinc.model.DailyProgressType.DAILY_INCREASE;
import static org.joelson.turf.dailyinc.model.DailyProgressType.DAILY_POWER_OF_TWO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DailyProgressTypeTest {

    @Test
    public void whenNegativeOrZeroDay_whenGetNeededVisits_theThrowsIlleagalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DAILY_INCREASE.getNeededVisits(-1));
        assertThrows(IllegalArgumentException.class, () -> DAILY_INCREASE.getNeededVisits(0));

        assertThrows(IllegalArgumentException.class, () -> DAILY_ADD.getNeededVisits(-1));
        assertThrows(IllegalArgumentException.class, () -> DAILY_ADD.getNeededVisits(0));

        assertThrows(IllegalArgumentException.class, () -> DAILY_FIBONACCI.getNeededVisits(-1));
        assertThrows(IllegalArgumentException.class, () -> DAILY_FIBONACCI.getNeededVisits(0));

        assertThrows(IllegalArgumentException.class, () -> DAILY_POWER_OF_TWO.getNeededVisits(-1));
        assertThrows(IllegalArgumentException.class, () -> DAILY_POWER_OF_TWO.getNeededVisits(0));
    }

    @Test
    public void whenGivenIncreasingForDay_whenDailyIncreaseGetNeededVisits_thenNeededVisitsIncreaseCorrectly() {
        int day = 1;
        int neededVisits = DAILY_INCREASE.getNeededVisits(day);
        assertEquals(1, neededVisits);
        while (neededVisits < 1000) {
            int nextDay = day + 1;
            int nextNeededVisits = DAILY_INCREASE.getNeededVisits(nextDay);
            assertEquals(neededVisits + 1, nextNeededVisits);
            day = nextDay;
            neededVisits = nextNeededVisits;
        }
    }

    @Test
    public void whenGivenIncreasingForDay_whenDailyAddGetNeededVisits_thenNeededVisitsIncreaseCorrectly() {
        int day = 1;
        int neededVisits = DAILY_INCREASE.getNeededVisits(day);
        assertEquals(1, neededVisits);
        while (neededVisits < 1000) {
            int nextDay = day + 1;
            int nextNeededVisits = DAILY_ADD.getNeededVisits(nextDay);
            assertEquals(neededVisits + nextDay, nextNeededVisits);
            day = nextDay;
            neededVisits = nextNeededVisits;
        }
    }

    @Test
    public void whenGivenIncreasingForDay_whenDailyFibonacciGetNeededVisits_thenNeededVisitsIncreaseCorrectly() {
        int prevDay = 1;
        int previousNeededVisits = DAILY_FIBONACCI.getNeededVisits(prevDay);
        assertEquals(1, previousNeededVisits);
        int day = 2;
        int neededVisits = DAILY_FIBONACCI.getNeededVisits(day);
        assertEquals(2, neededVisits);
        while (neededVisits < 1000) {
            int nextDay = day + 1;
            int nextNeededVisits = DAILY_FIBONACCI.getNeededVisits(nextDay);
            assertEquals(previousNeededVisits + neededVisits, nextNeededVisits);
            prevDay = day;
            previousNeededVisits = neededVisits;
            day = nextDay;
            neededVisits = nextNeededVisits;
        }
    }

    @Test
    public void whenGivenIncreasingForDay_whenDailyPowerOfTwoGetNeededVisits_thenNeededVisitsIncreaseCorrectly() {
        int day = 1;
        int neededVisits = DAILY_POWER_OF_TWO.getNeededVisits(day);
        assertEquals(1, neededVisits);
        while (neededVisits < 1000) {
            int nextDay = day + 1;
            int nextNeededVisits = DAILY_POWER_OF_TWO.getNeededVisits(nextDay);
            assertEquals(neededVisits * 2, nextNeededVisits);
            day = nextDay;
            neededVisits = nextNeededVisits;
        }
    }
}
