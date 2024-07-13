package org.joelson.turf.dailyinc.model;

import org.joelson.turf.dailyinc.util.ListTestUtil;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.joelson.turf.dailyinc.model.DailyProgressVisitsCache.calcAddDailyProgress;
import static org.joelson.turf.dailyinc.model.DailyProgressVisitsCache.calcFibonacciDailyProgress;
import static org.joelson.turf.dailyinc.model.DailyProgressVisitsCache.calcIncreaseDailyProgress;
import static org.joelson.turf.dailyinc.model.DailyProgressVisitsCache.calcPowerOfTwoDailyProgress;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DailyProgressVisitsCacheTest {

    private static final Instant START_TIME = Instant.now().truncatedTo(ChronoUnit.DAYS);
    private static final Instant PREV_TIME = START_TIME.minusSeconds(4711);
    private static final DailyProgress FIRST_DAILY_PROGRESS = new DailyProgress(0, 1, START_TIME);

    private static List<Instant> createVisitTimeListOfSize(int size) {
        return ListTestUtil.createListOfSize(START_TIME, size, time -> time, instant -> instant.plusSeconds(60));
    }

    @Test
    public void givenNoVisits_whenCalcIncreaseDailyProgress_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> calcIncreaseDailyProgress(null, null));
        assertThrows(IllegalArgumentException.class, () -> calcIncreaseDailyProgress(null, List.of()));

        assertThrows(IllegalArgumentException.class, () -> calcIncreaseDailyProgress(FIRST_DAILY_PROGRESS, null));
        assertThrows(IllegalArgumentException.class, () -> calcIncreaseDailyProgress(FIRST_DAILY_PROGRESS, List.of()));
    }

    @Test
    public void givenNoPreviousProgress_whenCalcIncreaseDailyProgress_thenFirstDayCompletedProgress() {
        for (int i = 1; i <= 100; i += 1) {
            assertEquals(FIRST_DAILY_PROGRESS, calcIncreaseDailyProgress(null, createVisitTimeListOfSize(i)));
        }
    }

    @Test
    public void givenPreviousProgress_whenCalcIncreaseDailyProgress_thenDailyProgress() {
        DailyProgress PREVIOUS_DAILY_PROGRESS = new DailyProgress(49, 50, PREV_TIME);

        for (int i = 1; i < 100; i += 1) {
            List<Instant> visitTimes = createVisitTimeListOfSize(i);
            DailyProgress dailyProgress = calcIncreaseDailyProgress(PREVIOUS_DAILY_PROGRESS, visitTimes);
            assertEquals(PREVIOUS_DAILY_PROGRESS.getCompleted(), dailyProgress.getPrevious());
            assertEquals(Math.min(i, PREVIOUS_DAILY_PROGRESS.getCompleted() + 1), dailyProgress.getCompleted());
            assertEquals(visitTimes.get(dailyProgress.getCompleted() - 1), dailyProgress.getTime());
        }
    }

    @Test
    public void givenNoVisits_whenCalcAddDailyProgress_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> calcAddDailyProgress(null, null));
        assertThrows(IllegalArgumentException.class, () -> calcAddDailyProgress(null, List.of()));

        assertThrows(IllegalArgumentException.class, () -> calcAddDailyProgress(FIRST_DAILY_PROGRESS, null));
        assertThrows(IllegalArgumentException.class, () -> calcAddDailyProgress(FIRST_DAILY_PROGRESS, List.of()));
    }

    @Test
    public void givenNoPreviousProgress_whenCalcAddDailyProgress_thenFirstDayCompletedProgress() {
        for (int i = 1; i <= 100; i += 1) {
            assertEquals(FIRST_DAILY_PROGRESS, calcAddDailyProgress(null, createVisitTimeListOfSize(i)));
        }
    }

    @Test
    public void givenPreviousProgress_whenCalcAddDailyProgress_thenDailyProgress() {
        DailyProgress PREVIOUS_DAILY_PROGRESS = new DailyProgress(7, 8, PREV_TIME);
        int UPPER_LIMIT_VISITS = DailyProgressType.DAILY_ADD.getNeededVisits(10) + 10;
        calcAddDailyProgress(new DailyProgress(10, 11, PREV_TIME), createVisitTimeListOfSize(1));

        for (int i = 1; i <= UPPER_LIMIT_VISITS; i += 1) {
            List<Instant> visitTimes = createVisitTimeListOfSize(i);
            DailyProgress dailyProgress = calcAddDailyProgress(PREVIOUS_DAILY_PROGRESS, visitTimes);
            assertEquals(PREVIOUS_DAILY_PROGRESS.getCompleted(), dailyProgress.getPrevious());
            int completed = dailyProgress.getCompleted();
            assertEquals(Math.min(completed, PREVIOUS_DAILY_PROGRESS.getCompleted() + 1), completed);
            int neededVisist = DailyProgressType.DAILY_ADD.getNeededVisits(completed);
            assertTrue(visitTimes.size() >= neededVisist);
            assertEquals(visitTimes.get(neededVisist - 1), dailyProgress.getTime());
        }
    }

    @Test
    public void givenNoVisits_whenCalcFibonacciDailyProgress_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> calcFibonacciDailyProgress(null, null));
        assertThrows(IllegalArgumentException.class, () -> calcFibonacciDailyProgress(null, List.of()));

        assertThrows(IllegalArgumentException.class, () -> calcFibonacciDailyProgress(FIRST_DAILY_PROGRESS, null));
        assertThrows(IllegalArgumentException.class, () -> calcFibonacciDailyProgress(FIRST_DAILY_PROGRESS, List.of()));
    }

    @Test
    public void givenNoPreviousProgress_whenCalcFibonacciDailyProgress_thenFirstDayCompletedProgress() {
        for (int i = 1; i <= 100; i += 1) {
            assertEquals(FIRST_DAILY_PROGRESS, calcFibonacciDailyProgress(null, createVisitTimeListOfSize(i)));
        }
    }

    @Test
    public void givenPreviousProgress_whenCalcFibonacciDailyProgress_thenDailyProgress() {
        DailyProgress PREVIOUS_DAILY_PROGRESS = new DailyProgress(7, 8, PREV_TIME);
        int UPPER_LIMIT_VISITS = DailyProgressType.DAILY_FIBONACCI.getNeededVisits(10) + 10;
        calcFibonacciDailyProgress(new DailyProgress(10, 11, PREV_TIME), createVisitTimeListOfSize(1));

        for (int i = 1; i <= UPPER_LIMIT_VISITS; i += 1) {
            List<Instant> visitTimes = createVisitTimeListOfSize(i);
            DailyProgress dailyProgress = calcFibonacciDailyProgress(PREVIOUS_DAILY_PROGRESS, visitTimes);
            assertEquals(PREVIOUS_DAILY_PROGRESS.getCompleted(), dailyProgress.getPrevious());
            int completed = dailyProgress.getCompleted();
            assertEquals(Math.min(completed, PREVIOUS_DAILY_PROGRESS.getCompleted() + 1), completed);
            int neededVisist = DailyProgressType.DAILY_FIBONACCI.getNeededVisits(completed);
            assertTrue(visitTimes.size() >= neededVisist);
            assertEquals(visitTimes.get(neededVisist - 1), dailyProgress.getTime());
        }
    }

    @Test
    public void givenNoVisits_whenCalcPowerOfTwoDailyProgress_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> calcPowerOfTwoDailyProgress(null, null));
        assertThrows(IllegalArgumentException.class, () -> calcPowerOfTwoDailyProgress(null, List.of()));

        assertThrows(IllegalArgumentException.class, () -> calcPowerOfTwoDailyProgress(FIRST_DAILY_PROGRESS, null));
        assertThrows(IllegalArgumentException.class, () -> calcPowerOfTwoDailyProgress(FIRST_DAILY_PROGRESS, List.of()));
    }

    @Test
    public void givenNoPreviousProgress_whenCalcPowerOfTwoDailyProgress_thenFirstDayCompletedProgress() {
        for (int i = 1; i <= 100; i += 1) {
            assertEquals(FIRST_DAILY_PROGRESS, calcPowerOfTwoDailyProgress(null, createVisitTimeListOfSize(i)));
        }
    }

    @Test
    public void givenPreviousProgress_whenCalcPowerOfTwoDailyProgress_thenDailyProgress() {
        DailyProgress PREVIOUS_DAILY_PROGRESS = new DailyProgress(7, 8, PREV_TIME);
        int UPPER_LIMIT_VISITS = DailyProgressType.DAILY_POWER_OF_TWO.getNeededVisits(10) + 10;
        calcPowerOfTwoDailyProgress(new DailyProgress(10, 11, PREV_TIME), createVisitTimeListOfSize(1));

        for (int i = UPPER_LIMIT_VISITS; i >= 1; i -= 1) {
            List<Instant> visitTimes = createVisitTimeListOfSize(i);
            DailyProgress dailyProgress = calcPowerOfTwoDailyProgress(PREVIOUS_DAILY_PROGRESS, visitTimes);
            assertEquals(PREVIOUS_DAILY_PROGRESS.getCompleted(), dailyProgress.getPrevious());
            int completed = dailyProgress.getCompleted();
            assertEquals(Math.min(completed, PREVIOUS_DAILY_PROGRESS.getCompleted() + 1), completed);
            int neededVisist = DailyProgressType.DAILY_POWER_OF_TWO.getNeededVisits(completed);
            assertTrue(visitTimes.size() >= neededVisist);
            assertEquals(visitTimes.get(neededVisist - 1), dailyProgress.getTime());
        }
    }
}
