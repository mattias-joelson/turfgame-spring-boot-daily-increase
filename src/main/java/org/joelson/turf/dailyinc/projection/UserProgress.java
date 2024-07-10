package org.joelson.turf.dailyinc.projection;

import org.joelson.turf.dailyinc.model.DailyProgress;

import java.time.Instant;

public interface UserProgress {

    Instant getDate();

    Integer getVisits();

    DailyProgress getIncrease();

    DailyProgress getAdd();

    DailyProgress getFibonacci();

    DailyProgress getPowerOfTwo();
}
