package org.joelson.turf.dailyinc.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class DailyProgressVisitsCache {

    private static final DailyProgressVisitsCacheInterface INCREASE_PROGRESS_VISITS_CACHE
            = new DailyProgressVisitsIncreaseCache();

    private static final DailyProgressVisitsCacheInterface ADD_PROGRESS_VISITS_CACHE
            = new DailyProgressVisitsListCache(DailyProgressType.DAILY_ADD::getNeededVisits);

    private static final DailyProgressVisitsCacheInterface FIBONACCI_PROGRESS_VISITS_CACHE
            = new DailyProgressVisitsListCache(DailyProgressType.DAILY_FIBONACCI::getNeededVisits);

    private static final DailyProgressVisitsCacheInterface POWER_OF_TWO_PROGRESS_VISITS_CACHE
            = new DailyProgressVisitsListCache(DailyProgressType.DAILY_POWER_OF_TWO::getNeededVisits);

    private DailyProgressVisitsCache() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated.");
    }

    public static DailyProgress calcIncreaseDailyProgress(DailyProgress previousProgress, List<Instant> visitTimes) {
        testVisitTimes(visitTimes);
        return INCREASE_PROGRESS_VISITS_CACHE.calcDailyProgress(previousProgress, visitTimes);
    }

    public static DailyProgress calcAddDailyProgress(DailyProgress previousProgress, List<Instant> visitTimes) {
        testVisitTimes(visitTimes);
        return ADD_PROGRESS_VISITS_CACHE.calcDailyProgress(previousProgress, visitTimes);
    }

    public static DailyProgress calcFibonacciDailyProgress(DailyProgress previousProgress, List<Instant> visitTimes) {
        testVisitTimes(visitTimes);
        return FIBONACCI_PROGRESS_VISITS_CACHE.calcDailyProgress(previousProgress, visitTimes);
    }

    public static DailyProgress calcPowerOfTwoDailyProgress(DailyProgress previousProgress, List<Instant> visitTimes) {
        testVisitTimes(visitTimes);
        return POWER_OF_TWO_PROGRESS_VISITS_CACHE.calcDailyProgress(previousProgress, visitTimes);
    }

    private static void testVisitTimes(List<Instant> visitTimes) {
        if (visitTimes == null || visitTimes.isEmpty()) {
            throw new IllegalArgumentException("visitTimes is null or empty");
        }
    }

    private interface DailyProgressVisitsCacheInterface {

        DailyProgress calcDailyProgress(DailyProgress previousProgress, List<Instant> visitTimes);
    }

    private static class DailyProgressVisitsIncreaseCache implements DailyProgressVisitsCacheInterface {

        @Override
        public DailyProgress calcDailyProgress(DailyProgress previousProgress, List<Instant> visitTimes) {
            if (previousProgress == null) {
                return new DailyProgress(0, 1, visitTimes.getFirst());
            }

            int noVisits = visitTimes.size();
            int previousDay = previousProgress.getCompleted();
            int dayCompleted = Math.min(previousDay + 1, noVisits);
            return new DailyProgress(previousDay, dayCompleted, visitTimes.get(dayCompleted - 1));
        }
    }

    private static class DailyProgressVisitsListCache implements DailyProgressVisitsCacheInterface {

        private final Function<Integer, Integer> neededVisits;
        private final List<Integer> visitsList;
        private final Map<Integer, Integer> visitsDayMap;

        public DailyProgressVisitsListCache(Function<Integer, Integer> neededVisits) {
            this.neededVisits = Objects.requireNonNull(neededVisits);
            visitsList = new ArrayList<>();
            visitsDayMap = new HashMap<>();
        }

        @Override
        public DailyProgress calcDailyProgress(DailyProgress previousProgress, List<Instant> visitTimes) {
            if (previousProgress == null) {
                return new DailyProgress(0, 1, visitTimes.getFirst());
            }

            int noVisits = visitTimes.size();
            int previousDay = previousProgress.getCompleted();
            int dayCompleted = calcDayCompleted(previousDay + 1, noVisits);
            int neededVisits = getNeededVisits(dayCompleted);
            return new DailyProgress(previousDay, dayCompleted, visitTimes.get(neededVisits - 1));
        }

        private int calcDayCompleted(Integer maxDay, int visits) {
            expandVisitsList(maxDay);
            Integer mappedDay = visitsDayMap.get(visits);
            if (mappedDay != null) {
                return mappedDay;
            }
            int day = binarySearch(maxDay, visits);
            visitsDayMap.put(visits, day);
            return day;
        }

        // from Collections.indexedBinarySearch()
        private int binarySearch(int maxDay, int visits) {
            int low = 0;
            int high = maxDay - 1;

            while (low <= high) {
                int mid = (low + high) >>> 1;
                int neededVisits = visitsList.get(mid);
                int cmp = Integer.compare(neededVisits, visits);

                if (cmp == 0) {
                    return mid + 1;
                } else if (cmp < 0) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
            return low;
        }

        private void expandVisitsList(Integer maxDay) {
            for (int day = visitsList.size(); day < maxDay; day += 1) {
                visitsList.add(neededVisits.apply(day + 1));
            }
        }

        private int getNeededVisits(int day) {
            return visitsList.get(day - 1);
        }
    }
}
