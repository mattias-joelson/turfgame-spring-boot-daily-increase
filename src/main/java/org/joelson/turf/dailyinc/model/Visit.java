package org.joelson.turf.dailyinc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "visits",
        indexes = { @Index(columnList = "zone_id", name = "idx_zone_id"), @Index(columnList = "user_id"),
                @Index(columnList = "time") }, uniqueConstraints = {
        @UniqueConstraint(name = "unique_zone_user_and_time", columnNames = { "zone_id", "user_id", "time" }) })
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToOne
    @JoinColumn(name = "zone_id", updatable = false, nullable = false)
    private Zone zone;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    private User user;

    @Column(updatable = false, nullable = false)
    private Instant time;

    @Column(updatable = false, nullable = false)
    private VisitType type;

    protected Visit() {
    }

    public Visit(Zone zone, User user, Instant time, VisitType type) {
        setZone(zone);
        setUser(user);
        setTime(time);
        setType(type);
    }

    public Long getId() {
        return id;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = Objects.requireNonNull(zone);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = Objects.requireNonNull(user);
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = Objects.requireNonNull(time);
    }

    public VisitType getType() {
        return type;
    }

    public void setType(VisitType type) {
        this.type = Objects.requireNonNull(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Visit visit) {
            return Objects.equals(id, visit.id) && Objects.equals(zone, visit.zone) && Objects.equals(user, visit.user)
                    && Objects.equals(time, visit.time) && type == visit.type;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("Visit[id=%d, zone=%s, user=%s, time=%s, type=%s]", id, zone, user, time, type);
    }
}
