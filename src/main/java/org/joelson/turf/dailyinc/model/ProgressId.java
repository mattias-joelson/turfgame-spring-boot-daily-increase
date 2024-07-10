package org.joelson.turf.dailyinc.model;

import java.time.Instant;
import java.util.Objects;

public class ProgressId {

    private Long user;
    private Instant date;

    protected ProgressId() {
    }

    public ProgressId(Long user, Instant date) {
        setUser(user);
        setDate(date);
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = ModelConstraintsUtil.isAboveZero(user);
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = ModelConstraintsUtil.isTruncatedToDays(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ProgressId that) {
            return Objects.equals(user, that.user) && Objects.equals(date, that.date);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, date);
    }

    @Override
    public String toString() {
        return String.format("ProgressId[user=%d, date=%s]", user, date);
    }
}
