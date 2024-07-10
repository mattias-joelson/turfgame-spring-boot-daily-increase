package org.joelson.turf.dailyinc.model;

import jakarta.persistence.Embeddable;

import java.time.Instant;
import java.util.Objects;

@Embeddable
public class DailyProgress {

    private Integer previous;
    private Integer completed;
    private Instant time;

    protected DailyProgress() {
    }

    public DailyProgress(
            Integer previous, Integer completed, Instant time) {
        this.previous = ModelConstraintsUtil.isEqualOrAboveZero(previous);
        setCompleted(completed);
        setTime(time);
    }

    public Integer getPrevious() {
        return previous;
    }

    public Integer getCompleted() {
        return completed;
    }

    public void setCompleted(Integer completed) {
        this.completed = ModelConstraintsUtil.isEqualOrBelow(
                ModelConstraintsUtil.isEqualOrAbove(ModelConstraintsUtil.isAboveZero(completed), this.completed),
                this.previous + 1);
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = ModelConstraintsUtil.isEqualOrAbove(ModelConstraintsUtil.isTruncatedToSeconds(time), this.time);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof DailyProgress that) {
            return Objects.equals(previous, that.previous) && Objects.equals(completed, that.completed)
                    && Objects.equals(time, that.time);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(previous, completed, time);
    }

    @Override
    public String toString() {
        return String.format("DailyProgress[previous=%d, completed=%d, time=%s]", previous, completed, time);
    }

    public String toInnerString(String type) {
        return String.format("%s.previous=%d, %s.completed=%d, %s.time=%s", type, previous, type, completed, type,
                time);
    }
}
