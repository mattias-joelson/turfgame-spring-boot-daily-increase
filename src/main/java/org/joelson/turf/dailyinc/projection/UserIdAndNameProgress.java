package org.joelson.turf.dailyinc.projection;

import org.joelson.turf.dailyinc.model.UserProgressTypeProgress;

import java.time.Instant;

public interface UserIdAndNameProgress {

    Instant getDate();

    Integer getVisits();

    UserProgressTypeProgress getIncrease();

    UserProgressTypeProgress getAdd();

    UserProgressTypeProgress getFibonacci();

    UserProgressTypeProgress getPowerOfTwo();
}
