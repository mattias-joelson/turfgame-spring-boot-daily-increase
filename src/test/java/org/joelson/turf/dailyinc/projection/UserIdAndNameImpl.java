package org.joelson.turf.dailyinc.projection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserIdAndNameImpl implements UserIdAndName {

    private final Long id;
    private final String name;

    @JsonCreator
    public UserIdAndNameImpl(@JsonProperty("id") Long id, @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
