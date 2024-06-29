package org.joelson.turf.dailyinc.model;

import java.time.Instant;
import java.util.Objects;

public class VisitId {

    private Long zone;
    private Long user;
    private Instant time;

    protected VisitId() {
    }

    public VisitId(Long zone, Long user, Instant time) {
        setZone(zone);
        setUser(user);
        setTime(time);
    }

    public Long getZone() {
        return zone;
    }

    public void setZone(Long zone) {
        this.zone = ModelConstraintsUtil.isAboveZero(zone);
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = ModelConstraintsUtil.isAboveZero(user);
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = ModelConstraintsUtil.isTruncatedToSeconds(time);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof VisitId visitId) {
            return Objects.equals(zone, visitId.zone) && Objects.equals(user, visitId.user)
                    && Objects.equals(time, visitId.time);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(zone, user, time);
    }

    @Override
    public String toString() {
        return String.format("VisitId[zone=%d, user=%d, time=%s]", zone, user, time);
    }
}
