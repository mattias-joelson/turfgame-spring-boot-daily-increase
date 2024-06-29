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
@IdClass(VisitId.class)
@Table(name = "visits", indexes = { @Index(name = "index_visits_zone_id", columnList = "zone_id"),
        @Index(name = "index_visits_user_id", columnList = "user_id"),
        @Index(name = "index_visits_time", columnList = "time") })
public class Visit {

    @Id
    @ManyToOne
    @JoinColumn(name = "zone_id", updatable = false, nullable = false)
    private Zone zone;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    private User user;

    @Id
    @Column(updatable = false, nullable = false)
    private Instant time;

    @Column(updatable = false, nullable = false)
    private VisitType type;

    protected Visit() {
    }

    public Visit(Zone zone, User user, Instant time, VisitType type) {
        this.zone = Objects.requireNonNull(zone);
        this.user = Objects.requireNonNull(user);
        this.time = ModelConstraintsUtil.isTruncatedToSeconds(time);
        this.type = Objects.requireNonNull(type);
    }

    public Zone getZone() {
        return zone;
    }

    public User getUser() {
        return user;
    }

    public Instant getTime() {
        return time;
    }

    public VisitType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Visit visit) {
            return Objects.equals(zone, visit.zone) && Objects.equals(user, visit.user)
                    && Objects.equals(time, visit.time) && type == visit.type;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(zone, user, time);
    }

    @Override
    public String toString() {
        return String.format("Visit[zone=%s, user=%s, time=%s, type=%s]", zone, user, time, type);
    }
}
