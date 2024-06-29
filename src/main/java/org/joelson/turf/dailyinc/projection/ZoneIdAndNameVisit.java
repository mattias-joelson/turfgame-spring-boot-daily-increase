package org.joelson.turf.dailyinc.projection;

import org.joelson.turf.dailyinc.model.VisitType;

import java.time.Instant;

public interface ZoneIdAndNameVisit {

    ZoneIdAndName getZone();

    Instant getTime();

    VisitType getType();
}
