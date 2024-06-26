package org.joelson.turf.dailyinc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "users", indexes = { @Index(columnList = "name") })
public class User {

    @Id
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Instant time;

    protected User() {
    }

    public User(Long id, String name, Instant time) {
        this.id = Objects.requireNonNull(id);
        setName(name);
        setTime(time);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = Objects.requireNonNull(time);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof User user) {
            return Objects.equals(id, user.id) && Objects.equals(name, user.name) && Objects.equals(time, user.time);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("User[id=%d, name='%s', time=%s]", id, name, time);
    }
}
