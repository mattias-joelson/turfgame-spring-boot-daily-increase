package org.joelson.turf.dailyinc.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joelson.turf.turfgame.FeedObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FeedTakeover extends FeedObject {

    private final FeedZone zone;
    private final FeedUser[] assists;

    @JsonCreator
    public FeedTakeover(
            @Nonnull @JsonProperty(value = "type", required = true) String type,
            @Nonnull @JsonProperty(value = "time", required = true) String time,
            @Nonnull @JsonProperty(value = "zone", required = true) FeedZone zone,
            @Nullable @JsonProperty("assists") FeedUser[] assists
    ) {
        super(type, time);
        this.zone = Objects.requireNonNull(zone);
        this.assists = assists;
    }

    @Override
    public String getType() {
        return "takeover";
    }

    public FeedZone getZone() {
        return zone;
    }

    public FeedUser[] getAssists() {
        return assists;
    }
}
