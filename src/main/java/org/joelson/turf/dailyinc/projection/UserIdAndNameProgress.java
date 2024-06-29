package org.joelson.turf.dailyinc.projection;

import org.joelson.turf.dailyinc.model.UserProgressType;

import java.time.Instant;

public interface UserIdAndNameProgress {

    UserIdAndName getUser();

    UserProgressType getType();

    Instant getDate();

    Integer getPreviousDayCompleted();

    Integer getDayCompleted();

    Instant getTimeCompleted();
}
