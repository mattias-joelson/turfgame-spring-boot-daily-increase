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
@IdClass(UserProgressId.class)
@Table(name = "user_progress", indexes = { @Index(name = "index_user_progress_user_id", columnList = "user_id"),
        @Index(name = "index_user_progress_type", columnList = "type"),
        @Index(name = "index_user_progress_date", columnList = "date"),
        @Index(name = "index_user_progress_day_completed", columnList = "day_completed"),
        @Index(name = "index_user_progress_time_completed", columnList = "time_completed") })
public class UserProgress {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    private User user;

    @Id
    @Column(updatable = false, nullable = false)
    private UserProgressType type;

    @Id
    @Column(updatable = false, nullable = false)
    private Instant date;

    @Column(name = "previous_day_completed", updatable = false, nullable = false)
    private Integer previousDayCompleted;

    @Column(name = "day_completed", nullable = false)
    private Integer dayCompleted;

    @Column(name = "time_completed", nullable = false)
    private Instant timeCompleted;

    protected UserProgress() {
    }

    public UserProgress(
            User user, UserProgressType type, Instant date, Integer previousDayCompleted, Integer dayCompleted,
            Instant timeCompleted) {
        setUser(user);
        setType(type);
        setDate(date);
        setPreviousDayCompleted(previousDayCompleted);
        setDayCompleted(dayCompleted);
        setTimeCompleted(timeCompleted);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
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

    public Integer getPreviousDayCompleted() {
        return previousDayCompleted;
    }

    public void setPreviousDayCompleted(Integer previousDayCompleted) {
        this.previousDayCompleted = Objects.requireNonNull(previousDayCompleted);
    }

    public Integer getDayCompleted() {
        return dayCompleted;
    }

    public void setDayCompleted(Integer dayCompleted) {
        this.dayCompleted = Objects.requireNonNull(dayCompleted);
    }

    public Instant getTimeCompleted() {
        return timeCompleted;
    }

    public void setTimeCompleted(Instant timeCompleted) {
        this.timeCompleted = Objects.requireNonNull(timeCompleted);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof UserProgress that) {
            return Objects.equals(user, that.user) && type == that.type && Objects.equals(date, that.date)
                    && Objects.equals(previousDayCompleted, that.previousDayCompleted)
                    && Objects.equals(dayCompleted, that.dayCompleted)
                    && Objects.equals(timeCompleted, that.timeCompleted);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, type, date);
    }

    @Override
    public String toString() {
        return String.format(
                "UserProgress[user=%s, type=%s, date=%s, previousDayCompleted=%d, dayCompleted=%s, timeCompleted=%s",
                user, type, date, previousDayCompleted, dayCompleted, timeCompleted);
    }
}
