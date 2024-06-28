package org.joelson.turf.dailyinc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Entity
@IdClass(UserVisitsId.class)
@Table(name = "user_visits", indexes = { @Index(name = "index_user_visits_user_id", columnList = "user_id"),
        @Index(name = "index_user_visits_date", columnList = "date"),
        @Index(name = "index_user_visits_visits", columnList = "visits") })
public class UserVisits {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    private User user;

    @Id
    @Column(updatable = false, nullable = false)
    private Instant date;

    @Column(nullable = false)
    private Integer visits;

    protected UserVisits() {
    }

    public UserVisits(User user, Instant date, Integer visits) {
        setUser(user);
        setDate(date);
        setVisits(visits);
    }

    private static Instant instantTruncatedToDate(Instant instant) {
        if (!instant.truncatedTo(ChronoUnit.DAYS).equals(instant)) {
            throw new IllegalArgumentException(String.format("Instant %s not truncated to date.", instant));
        }
        return instant;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = Objects.requireNonNull(user);
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = instantTruncatedToDate(Objects.requireNonNull(date));
    }

    public Integer getVisits() {
        return visits;
    }

    public void setVisits(Integer visits) {
        this.visits = Objects.requireNonNull(visits);
        if (visits <= 0) {
            throw new IllegalArgumentException("Visits equal to or below 0: " + visits);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof UserVisits that) {
            return Objects.equals(user, that.user) && Objects.equals(date, that.date)
                    && Objects.equals(visits, that.visits);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, date);
    }

    @Override
    public String toString() {
        return String.format("UserVisits[user=%s, date=%s, visits=%d]", user, date, visits);
    }
}
