package org.joelson.turf.dailyinc.projection;

import java.time.Instant;

public interface UserIdAndNameVisits {

    UserIdAndName getUser();

    Instant getDate();

    Integer getVisits();
}
