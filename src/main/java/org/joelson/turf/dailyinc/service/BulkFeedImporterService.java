package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.Visit;
import org.joelson.turf.dailyinc.model.VisitType;
import org.joelson.turf.dailyinc.model.Zone;
import org.joelson.turf.turfgame.FeedObject;
import org.joelson.turf.turfgame.apiv5.FeedTakeover;
import org.joelson.turf.turfgame.util.FeedsPathComparator;
import org.joelson.turf.turfgame.util.FeedsReader;
import org.joelson.turf.util.FilesUtil;
import org.joelson.turf.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@Service
public class BulkFeedImporterService {

    private static final Logger logger = LoggerFactory.getLogger(BulkFeedImporterService.class);

    private int filesHandled = 0;

    private int importedTakes = 0;
    private int importedRevisits = 0;
    private int importedAssists = 0;

    private int skippedTakes = 0;
    private int skippedRevisits = 0;
    private int skippedAssists = 0;

    private final FeedsReader feedsReader;

    @Autowired
    UserService userService;

    @Autowired
    VisitService visitService;

    @Autowired
    ZoneService zoneService;

    public BulkFeedImporterService() {
        feedsReader = new FeedsReader(Map.of("takeover", FeedTakeover.class));
    }

    public void importFeed(String filename) {
        try {
            FilesUtil.forEachFile(Path.of(filename), true, new FeedsPathComparator(), this::addFeedObjects);
        } catch (IOException e) {
            logger.error(String.format("Unable to import feed %s:", filename), e);
        }
    }

    private void addFeedObjects(Path path) {
        feedsReader.handleFeedObjectFile(path, this::logEvery100thPath, this::handleFeedObject);
    }

    private void logEvery100thPath(Path path) {
        if (filesHandled % 100 == 0) {
            logger.info("Reading path {} (imported takes={}, revisits={}, assists={}, skipped takes={}, revisits={}, assists={})",
                    path, importedTakes, importedRevisits, importedAssists, skippedTakes, skippedRevisits, skippedAssists);
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

    void handleTakeover(FeedTakeover feedTakeover) {
        Instant time = TimeUtil.turfAPITimestampToInstant(feedTakeover.getTime());
        Zone zone = getUpdateOrCreate(feedTakeover.getZone(), time);
        User user = getUpdateOrCreate(feedTakeover.getZone().getCurrentOwner(), time);
        User previousUser = getUpdateOrCreate(feedTakeover.getZone().getPreviousOwner(), time);
        VisitType type = (previousUser == null || !Objects.equals(user.getId(), previousUser.getId()))
                ? VisitType.TAKE : VisitType.REVISIT;
        Visit existingVisit = visitService.getVisit(zone, user, time);
        org.joelson.turf.turfgame.apiv5.User[] assists = feedTakeover.getAssists();
        if (existingVisit != null) {
            logger.trace("Skipping existing visit {}...", existingVisit);
            if (type == VisitType.TAKE) {
                skippedTakes += 1;
            } else {
                skippedRevisits += 1;
            }
            if (assists != null) {
                skippedAssists += assists.length;
            }
            return;
        }
        addVisit(zone, user, time, type);
        if (type == VisitType.TAKE) {
            importedTakes += 1;
        } else {
            importedRevisits += 1;
        }
        if (assists != null) {
            Arrays.stream(assists).map(a -> getUpdateOrCreate(a, time))
                    .forEach(a -> addVisit(zone, a, time, VisitType.ASSIST));
            importedAssists += assists.length;
        }
    }

    private void addVisit(Zone zone, User user, Instant time, VisitType type) {
        Visit visit = visitService.create(zone, user, time, type);
        logger.trace("Added visit {}", visit);
    }

    public void calculateProgress() {
        logger.error("Code missing here!");
    }
}
