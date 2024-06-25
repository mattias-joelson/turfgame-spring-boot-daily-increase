package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.Assist;
import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.Visit;
import org.joelson.turf.dailyinc.model.Zone;
import org.joelson.turf.turfgame.FeedObject;
import org.joelson.turf.turfgame.apiv5.FeedChat;
import org.joelson.turf.turfgame.apiv5.FeedMedal;
import org.joelson.turf.turfgame.apiv5.FeedTakeover;
import org.joelson.turf.turfgame.apiv5.FeedZone;
import org.joelson.turf.turfgame.apiv5util.FeedsReader;
import org.joelson.turf.turfgame.util.FeedsPathComparator;
import org.joelson.turf.util.FilesUtil;
import org.joelson.turf.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;

@Service
public class FeedImporterService {

    Logger logger = LoggerFactory.getLogger(FeedImporterService.class);

    @Autowired
    UserService userService;

    @Autowired
    VisitService visitService;

    @Autowired
    ZoneService zoneService;

    public void importFeed(String filename) {
        try {
            FilesUtil.forEachFile(Path.of(filename), true, new FeedsPathComparator(), this::addFeedObjects);
        } catch (IOException e) {
            logger.error(String.format("Unable to import feed %s:", filename), e);
        }
    }

    private void addFeedObjects(Path path) {
        new FeedsReader().handleFeedObjectFile(path, p -> {
        }, this::handleFeedObject);
    }

    private void handleFeedObject(FeedObject feedObject) {
        Instant time = TimeUtil.turfAPITimestampToInstant(feedObject.getTime());
        switch (feedObject) {
            case FeedZone feedZone -> addZone(feedZone.getZone(), time);
            case FeedChat feedChat -> addUser(feedChat.getSender(), time);
            case FeedMedal feedMedal -> addUser(feedMedal.getUser(), time);
            case FeedTakeover feedTakeover -> handleTakeover(feedTakeover, time);
            default -> throw new IllegalArgumentException(
                    String.format("Unknown FeedObject type %s.", feedObject.getType()));
        }
    }

    private void addUser(org.joelson.turf.turfgame.apiv5.User userV5, Instant instant) {
        User user = getUpdateOrCreate(userV5, instant);
        logger.trace(String.format("Added user %s", user));
    }

    private User getUpdateOrCreate(org.joelson.turf.turfgame.apiv5.User userV5, Instant time) {
        if (userV5 == null) {
            return null;
        }
        return userService.getUpdateOrCreate((long) userV5.getId(), userV5.getName(), time);
    }

    private void addZone(org.joelson.turf.turfgame.apiv5.Zone zoneV5, Instant instant) {
        Zone zone = getUpdateOrCreate(zoneV5, instant);
        logger.trace(String.format("Added zone %s", zone));
    }

    private Zone getUpdateOrCreate(org.joelson.turf.turfgame.apiv5.Zone zoneV5, Instant instant) {
        return zoneService.getUpdateOrCreate((long) zoneV5.getId(), zoneV5.getName(), instant);
    }

    private void handleTakeover(FeedTakeover feedTakeover, Instant time) {
        Zone zone = getUpdateOrCreate(feedTakeover.getZone(), time);
        Visit visit = visitService.getVisit(zone, time);
        if (visit == null) {
            User user = getUpdateOrCreate(feedTakeover.getZone().getCurrentOwner(), time);
            User previousUser = getUpdateOrCreate(feedTakeover.getZone().getPreviousOwner(), time);
            boolean takeover = previousUser == null || !Objects.equals(user.getId(), previousUser.getId());
            visit = visitService.createVisit(zone, user, time, takeover);
            logger.trace(String.format("Added visit %s", visit));
            if (feedTakeover.getAssists() != null) {
                logger.info(String.format("assister to add: %d", feedTakeover.getAssists().length));
                for (org.joelson.turf.turfgame.apiv5.User assisterV5 : feedTakeover.getAssists()) {
                    User assister = getUpdateOrCreate(assisterV5, time);
                    Assist assist = visitService.addAssist(visit, assister);
                    logger.info(String.format("Added assist %s", assist));
                }
            }
        }
    }
}
