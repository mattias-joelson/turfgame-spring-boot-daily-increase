package org.joelson.turf.dailyinc.projection;

import org.joelson.turf.dailyinc.model.VisitType;

import java.time.Instant;

public interface UserIdAndNameVisit {

    UserIdAndName getUser();

    Instant getTime();

    VisitType getType();
}
