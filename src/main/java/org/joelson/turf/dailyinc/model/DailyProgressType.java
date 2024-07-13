package org.joelson.turf.dailyinc.model;

import java.util.function.Function;

public enum DailyProgressType {

    DAILY_INCREASE(integer -> integer),
    DAILY_ADD(DailyProgressType::getDailyAddVisits),
    DAILY_FIBONACCI(DailyProgressType::getDailyFibonacciVisits),
    DAILY_POWER_OF_TWO(DailyProgressType::getDailyPowerOfTwoVisits);

    private final Function<Integer, Integer> visitsNeeded;

    DailyProgressType(Function<Integer, Integer> visitsNeeded) {
        this.visitsNeeded = visitsNeeded;
    }

    private static int getDailyAddVisits(int forDay) {
        int visits = 0;
        for (int day = 1; day <= forDay; day += 1) {
            visits += day;
        }
        return visits;
    }

    private static int getDailyFibonacciVisits(int forDay) {
        if (forDay <= 2) {
            return forDay;
        }
        int[] visits = new int[forDay];
        visits[0] = 1;
        visits[1] = 2;
        for (int day = 2; day < forDay; day += 1) {
            visits[day] = visits[day - 2] + visits[day - 1];
        }
        return visits[forDay - 1];
    }

    private static int getDailyPowerOfTwoVisits(int forDay) {
        int visits = 1;
        for (int day = 1; day < forDay; day += 1) {
            visits *= 2;
        }
        return visits;
    }

    public int getNeededVisits(int forDay) {
        if (forDay < 1) {
            throw new IllegalArgumentException("forDay must be at least 1");
        }
        return visitsNeeded.apply(forDay);
    }
}
