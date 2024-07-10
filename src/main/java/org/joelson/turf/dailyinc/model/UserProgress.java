package org.joelson.turf.dailyinc.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
        @Index(name = "index_user_progress_date", columnList = "date"),
        @Index(name = "index_user_progress_inc_comp", columnList = "inc_comp"),
        @Index(name = "index_user_progress_inc_time", columnList = "inc_time"),
        @Index(name = "index_user_progress_add_comp", columnList = "add_comp"),
        @Index(name = "index_user_progress_add_time", columnList = "add_time"),
        @Index(name = "index_user_progress_fib_comp", columnList = "fib_comp"),
        @Index(name = "index_user_progress_fib_time", columnList = "fib_time"),
        @Index(name = "index_user_progress_pow_comp", columnList = "pow_comp"),
        @Index(name = "index_user_progress_pow_time", columnList = "pow_time") })
public class UserProgress {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    private User user;

    @Id
    @Column(updatable = false, nullable = false)
    private Instant date;

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "previous",
            column = @Column(name = "inc_prev", updatable = false, nullable = false)),
            @AttributeOverride(name = "completed", column = @Column(name = "inc_comp", nullable = false)),
            @AttributeOverride(name = "time", column = @Column(name = "inc_time", nullable = false)) })
    private UserProgressTypeProgress increase;

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "previous",
            column = @Column(name = "add_prev", updatable = false, nullable = false)),
            @AttributeOverride(name = "completed", column = @Column(name = "add_comp", nullable = false)),
            @AttributeOverride(name = "time", column = @Column(name = "add_time", nullable = false)) })
    private UserProgressTypeProgress add;

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "previous",
            column = @Column(name = "fib_prev", updatable = false, nullable = false)),
            @AttributeOverride(name = "completed", column = @Column(name = "fib_comp", nullable = false)),
            @AttributeOverride(name = "time", column = @Column(name = "fib_time", nullable = false)) })
    private UserProgressTypeProgress fibonacci;

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "previous",
            column = @Column(name = "pow_prev", updatable = false, nullable = false)),
            @AttributeOverride(name = "completed", column = @Column(name = "pow_comp", nullable = false)),
            @AttributeOverride(name = "time", column = @Column(name = "pow_time", nullable = false)) })
    private UserProgressTypeProgress powerOfTwo;


    protected UserProgress() {
    }

    public UserProgress(
            User user, Instant date, UserProgressTypeProgress increase, UserProgressTypeProgress add,
            UserProgressTypeProgress fibonacci, UserProgressTypeProgress powerOfTwo) {
        this.user = Objects.requireNonNull(user);
        this.date = ModelConstraintsUtil.isTruncatedToDays(date);
        this.increase = Objects.requireNonNull(increase);
        this.add = Objects.requireNonNull(add);
        this.fibonacci = Objects.requireNonNull(fibonacci);
        this.powerOfTwo = Objects.requireNonNull(powerOfTwo);
    }

    public User getUser() {
        return user;
    }

    public Instant getDate() {
        return date;
    }


    public UserProgressTypeProgress getIncrease() {
        return increase;
    }

    public UserProgressTypeProgress getAdd() {
        return add;
    }

    public UserProgressTypeProgress getFibonacci() {
        return fibonacci;
    }

    public UserProgressTypeProgress getPowerOfTwo() {
        return powerOfTwo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof UserProgress that) {
            return Objects.equals(user, that.user) && Objects.equals(date, that.date)
                    && Objects.equals(increase, that.increase) && Objects.equals(add, that.add)
                    && Objects.equals(fibonacci, that.fibonacci) && Objects.equals(powerOfTwo, that.powerOfTwo);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, date);
    }

    @Override
    public String toString() {
        return String.format("UserProgress[user=%s, type=%s, %s, %s, %s, %s]", user, date,
                increase.toInnerString("increase"), add.toInnerString("add"), fibonacci.toInnerString("fibonacci"),
                powerOfTwo.toInnerString("powerOfTwo"));
    }
}
