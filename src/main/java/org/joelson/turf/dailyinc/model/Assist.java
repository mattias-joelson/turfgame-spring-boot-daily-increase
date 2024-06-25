package org.joelson.turf.dailyinc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@IdClass(AssistId.class)
@Table(name = "assists", indexes = { @Index(columnList = "visit_id"), @Index(columnList = "user_id") })
public class Assist {

    @Id
    @ManyToOne
    @JoinColumn(name= "visit_id", updatable = false, nullable = false)
    Visit visit;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    User user;

    protected Assist() {
    }

    public Assist(Visit visit, User user) {
        setVisit(visit);
        setUser(user);
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Assist assist) {
            return Objects.equals(visit, assist.visit) && Objects.equals(user, assist.user);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(visit, user);
    }

    @Override
    public String toString() {
        return String.format("Assist[visit=%s, user=%s]", visit, user);
    }
}
