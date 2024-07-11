package org.joelson.turf.dailyinc.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FeedUser(int id, String name) {

    @JsonCreator
    public FeedUser(
            @JsonProperty(value = "id", required = true) int id,
            @Nonnull @JsonProperty(value = "name", required = true) String name
    ) {
        this.id = id;
        this.name = name;
    }
}
