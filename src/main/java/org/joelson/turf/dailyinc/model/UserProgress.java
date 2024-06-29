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
        this.user = Objects.requireNonNull(user);
        this.type = Objects.requireNonNull(type);
        this.date = ModelConstraintsUtil.isTruncatedToDays(date);
        this.previousDayCompleted = ModelConstraintsUtil.isEqualOrAboveZero(previousDayCompleted);
        setDayCompleted(dayCompleted);
        setTimeCompleted(timeCompleted);
    }

    public User getUser() {
        return user;
    }

    public UserProgressType getType() {
        return type;
    }

    public Instant getDate() {
        return date;
    }

    public Integer getPreviousDayCompleted() {
        return previousDayCompleted;
    }

    public Integer getDayCompleted() {
        return dayCompleted;
    }

    public void setDayCompleted(Integer dayCompleted) {
        this.dayCompleted = ModelConstraintsUtil.isEqualOrBelow(
                ModelConstraintsUtil.isEqualOrAbove(ModelConstraintsUtil.isAboveZero(dayCompleted), this.dayCompleted),
                this.previousDayCompleted + 1);
    }

    public Instant getTimeCompleted() {
        return timeCompleted;
    }

    public void setTimeCompleted(Instant timeCompleted) {
        this.timeCompleted =
                ModelConstraintsUtil.isEqualOrAbove(ModelConstraintsUtil.isTruncatedToSeconds(timeCompleted), this.timeCompleted);
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
