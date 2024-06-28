package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.Visit;
import org.joelson.turf.dailyinc.model.VisitType;
import org.joelson.turf.dailyinc.model.Zone;
import org.joelson.turf.turfgame.FeedObject;
import org.joelson.turf.turfgame.apiv5.FeedTakeover;
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
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Objects;

@Service
public class FeedImporterService {

    Logger logger = LoggerFactory.getLogger(FeedImporterService.class);
    private int filesHandled = 0;

    @Autowired
    UserService userService;

    @Autowired
    UserProgressService userProgressService;

    @Autowired
    UserVisitsService userVisitsService;

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
        new FeedsReader().handleFeedObjectFile(path, this::logEvery100thPath, this::handleFeedObject);
    }

    private void logEvery100thPath(Path path) {
        if (filesHandled % 100 == 0) {
            logger.info(String.format("Reading path %s", path));
        }
        filesHandled += 1;
    }

    private void handleFeedObject(FeedObject feedObject) {
        if (feedObject instanceof FeedTakeover feedTakeover) {
            handleTakeover(feedTakeover);
        }
    }

    private User getUpdateOrCreate(org.joelson.turf.turfgame.apiv5.User userV5, Instant time) {
        if (userV5 == null) {
            return null;
        }
        return userService.getUpdateOrCreate((long) userV5.getId(), userV5.getName(), time);
    }

    private Zone getUpdateOrCreate(org.joelson.turf.turfgame.apiv5.Zone zoneV5, Instant time) {
        return zoneService.getUpdateOrCreate((long) zoneV5.getId(), zoneV5.getName(), time);
    }

    private void handleTakeover(FeedTakeover feedTakeover) {
        Instant time = TimeUtil.turfAPITimestampToInstant(feedTakeover.getTime());
        Instant date = time.truncatedTo(ChronoUnit.DAYS);
        Zone zone = getUpdateOrCreate(feedTakeover.getZone(), time);
        User user = getUpdateOrCreate(feedTakeover.getZone().getCurrentOwner(), time);
        User previousUser = getUpdateOrCreate(feedTakeover.getZone().getPreviousOwner(), time);
        VisitType type = (previousUser == null || !Objects.equals(user.getId(), previousUser.getId()))
                ? VisitType.TAKEOVER : VisitType.REVISIT;
        Visit existingVisit = visitService.getVisit(zone, user, time);
        if (existingVisit != null) {
            logger.trace(String.format("Skipping existing visit %s...", existingVisit));
            return;
        }
        addVisit(zone, user, time, type, date);
        if (feedTakeover.getAssists() != null) {
            Arrays.stream(feedTakeover.getAssists()).map(a -> getUpdateOrCreate(a, time))
                    .forEach(a -> addVisit(zone, a, time, VisitType.ASSIST, date));
        }
    }

    private void addVisit(Zone zone, User user, Instant time, VisitType type, Instant date) {
        Visit visit = visitService.create(zone, user, time, type);
        logger.trace(String.format("Added visit %s", visit));
        int visits = userVisitsService.increaseUserVisits(user, date);
        logger.trace(String.format("Visits %d @ %s", visits, date));
        int maxDayCompleted = userProgressService.increaseUserProgress(user, date, visits, time);
        logger.trace(String.format("Max day completed %d @ %s", maxDayCompleted, time));
    }
}
