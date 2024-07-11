package org.joelson.turf.dailyinc.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FeedZone(int id, String name, FeedUser previousOwner, FeedUser currentOwner) {

    @JsonCreator
    public FeedZone(
            @JsonProperty(value = "id", required = true) int id,
            @Nonnull @JsonProperty(value = "name", required = true) String name,
            @Nullable @JsonProperty("previousOwner") FeedUser previousOwner,
            @Nonnull @JsonProperty(value = "currentOwner", required = true) FeedUser currentOwner
    ) {
        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.previousOwner = previousOwner;
        this.currentOwner = Objects.requireNonNull(currentOwner);
    }

    public boolean isTake() {
        return previousOwner == null || previousOwner.id() != currentOwner.id();
    }
}
