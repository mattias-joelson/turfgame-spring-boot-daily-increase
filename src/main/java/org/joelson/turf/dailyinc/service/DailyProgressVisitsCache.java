package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.DailyProgress;
import org.joelson.turf.dailyinc.model.DailyProgressType;
import org.joelson.turf.dailyinc.model.Visit;

import java.util.ArrayList;
import java.util.List;
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

    public static DailyProgress calcIncreaseDailyProgress(DailyProgress previousProgress, List<Visit> visits) {
        if (visits == null || visits.isEmpty()) {
            throw new IllegalArgumentException("visits is null or empty");
        }
        return INCREASE_PROGRESS_VISITS_CACHE.calcDailyProgress(previousProgress, visits);
    }

    public static DailyProgress calcAddDailyProgress(DailyProgress previousProgress, List<Visit> visits) {
        if (visits == null || visits.isEmpty()) {
            throw new IllegalArgumentException("visits is null or empty");
        }
        return ADD_PROGRESS_VISITS_CACHE.calcDailyProgress(previousProgress, visits);
    }

    public static DailyProgress calcFibonacciDailyProgress(DailyProgress previousProgress, List<Visit> visits) {
        if (visits == null || visits.isEmpty()) {
            throw new IllegalArgumentException("visits is null or empty");
        }
        return FIBONACCI_PROGRESS_VISITS_CACHE.calcDailyProgress(previousProgress, visits);
    }

    public static DailyProgress calcPowerOfTwoDailyProgress(DailyProgress previousProgress, List<Visit> visits) {
        if (visits == null || visits.isEmpty()) {
            throw new IllegalArgumentException("visits is null or empty");
        }
        return POWER_OF_TWO_PROGRESS_VISITS_CACHE.calcDailyProgress(previousProgress, visits);
    }

    private interface DailyProgressVisitsCacheInterface {

        DailyProgress calcDailyProgress(DailyProgress previousProgress, List<Visit> visits);
    }

    private static class DailyProgressVisitsIncreaseCache implements DailyProgressVisitsCacheInterface {

        @Override
        public DailyProgress calcDailyProgress(DailyProgress previousProgress, List<Visit> visits) {
            if (previousProgress == null) {
                return new DailyProgress(0, 1, visits.getFirst().getTime());
            }

            int noVisits = visits.size();
            int previousDay = previousProgress.getCompleted();
            int dayCompleted = Math.min(previousDay + 1, noVisits);
            return new DailyProgress(previousDay, dayCompleted, visits.get(dayCompleted - 1).getTime());
        }
    }

    private static class DailyProgressVisitsListCache implements DailyProgressVisitsCacheInterface {

        private final Function<Integer, Integer> neededVisits;
        private final List<Integer> visitsList;

        public DailyProgressVisitsListCache(Function<Integer, Integer> neededVisits) {
            this.neededVisits = Objects.requireNonNull(neededVisits);
            visitsList = new ArrayList<>();
        }

        @Override
        public DailyProgress calcDailyProgress(DailyProgress previousProgress, List<Visit> visits) {
            if (previousProgress == null) {
                return new DailyProgress(0, 1, visits.getFirst().getTime());
            }

            int noVisits = visits.size();
            int previousDay = previousProgress.getCompleted();
            int dayCompleted = calcDayCompleted(previousDay + 1, noVisits);
            int neededVisits = getNeededVisits(dayCompleted);
            return new DailyProgress(previousDay, dayCompleted, visits.get(neededVisits - 1).getTime());
        }

        private int calcDayCompleted(Integer maxDay, int visits) {
            expandVisitsList(maxDay);
            return binarySearch(maxDay, visits);
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
