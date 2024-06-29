package org.joelson.turf.dailyinc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "zones", indexes = { @Index(name = "index_zones_name", columnList = "name") })
public class Zone {

    @Id
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Instant time;

    protected Zone() {
    }

    public Zone(Long id, String name, Instant time) {
        this.id = ModelConstraintsUtil.isAboveZero(id);
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
        this.time = ModelConstraintsUtil.isEqualOrAbove(ModelConstraintsUtil.isTruncatedToSeconds(time), this.time);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Zone zone) {
            return Objects.equals(id, zone.id) && Objects.equals(name, zone.name) && Objects.equals(time, zone.time);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("Zone[id=%d, name='%s', time=%s]", id, name, time);
    }
}
