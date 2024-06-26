package org.joelson.turf.dailyinc.model;

import java.time.Instant;
import java.util.Objects;

public class UserProgressId {

    private Long user;
    private UserProgressType type;
    private Instant date;

    protected UserProgressId() {
    }

    public UserProgressId(Long user, UserProgressType type, Instant date) {
        setUser(user);
        setType(type);
        setDate(date);
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = Objects.requireNonNull(user);
    }

    public UserProgressType getType() {
        return type;
    }

    public void setType(UserProgressType type) {
        this.type = Objects.requireNonNull(type);
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = Objects.requireNonNull(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof UserProgressId that) {
            return Objects.equals(user, that.user) && type == that.type && Objects.equals(date, that.date);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, type, date);
    }

    @Override
    public String toString() {
        return String.format("UserProgressId[user=%d, type=%s, date=%s", user, type, date);
    }
}
