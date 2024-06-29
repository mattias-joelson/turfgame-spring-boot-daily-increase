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
        this.user = Objects.requireNonNull(user);
        this.date = ModelConstraintsUtil.isTruncatedToDays(date);
        setVisits(visits);
    }

    public User getUser() {
        return user;
    }

    public Instant getDate() {
        return date;
    }

    public Integer getVisits() {
        return visits;
    }

    public void setVisits(Integer visits) {
        this.visits = ModelConstraintsUtil.isEqualOrAbove(ModelConstraintsUtil.isAboveZero(visits), this.visits);
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
