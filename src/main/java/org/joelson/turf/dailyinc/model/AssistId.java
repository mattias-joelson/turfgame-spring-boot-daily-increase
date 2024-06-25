package org.joelson.turf.dailyinc.model;

import java.util.Objects;

public class AssistId {

    private Visit visit;
    private User user;

    protected AssistId() {
    }

    public AssistId(Visit visit, User user) {
        setVisit(visit);
        setUser(user);
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = Objects.requireNonNull(visit);
    }

    public User getUser() {
        return user;
    }

    private void setUser(User user) {
        this.user = Objects.requireNonNull(user);
    }

    @Override
    public boolean equals(Object obj) {
        throw new RuntimeException();
    }

    @Override
    public int hashCode() {
        throw new RuntimeException();
    }

    @Override
    public String toString() {
        throw new RuntimeException();
    }
}
