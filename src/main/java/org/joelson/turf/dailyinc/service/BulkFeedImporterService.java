package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.FeedTakeover;
import org.joelson.turf.dailyinc.model.FeedUser;
import org.joelson.turf.dailyinc.model.FeedZone;
import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.Visit;
import org.joelson.turf.dailyinc.model.VisitType;
import org.joelson.turf.dailyinc.model.Zone;
import org.joelson.turf.turfgame.FeedObject;
import org.joelson.turf.turfgame.util.FeedsReader;
import org.joelson.turf.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private final Map<Integer, User> users = new HashMap<>();
    private int foundUsers = 0;
    private int insertedUsers = 0;
    private final Map<Integer, Zone> zones = new HashMap<>();
    private int foundZones = 0;
    private int insertedZones = 0;
    private final Set<ZoneIdTime> visits = new HashSet<>();
    private int foundVisits = 0;
    private int insertedVisits = 0;

    private final FeedsReader feedsReader;

    @Autowired
    UserService userService;

    @Autowired
    VisitService visitService;

    @Autowired
    ZoneService zoneService;

    public BulkFeedImporterService() {
        feedsReader = new FeedsReader(Map.of("takeover", FeedTakeover.class), true);
    }

    public void importFeeds(String[] filenames) {
        List<Path> paths = new ArrayList<>(filenames.length);
        for (String filename : filenames) {
            Path path = Path.of(filename);
            if (Files.exists(path) && Files.isRegularFile(path)) {
                paths.add(path);
            } else {
                logger.error("Path {} does not exist or is not a regular file.", path);
            }
        }
        // sort reversed
        paths.sort((p1, p2) -> p2.compareTo(p1));
        for (Path path : paths) {
            logger.info("Importing data from {}", path);
            feedsReader.handleFeedObjectFile(path, this::logEvery100thPath, this::handleFeedObject);
        }
        calculateProgress();
    }

    private void logEvery100thPath(Path path) {
        if (!path.getFileName().toString().contains("takeover"))
        {
            return;
        }
        if (filesHandled % 100 == 0) {
            logger.info("Reading path {} (imported takes={}, revisits={}, assists={}, skipped takes={}, revisits={}, assists={}, visits.size()={}, found={}, inserted={}, zones.size()={}, found={}, inserted={}, users.size()={}, found={}, inserted={})",
                    path, importedTakes, importedRevisits, importedAssists, skippedTakes, skippedRevisits, skippedAssists, visits.size(), foundVisits, insertedVisits, zones.size(), foundZones, insertedZones, users.size(), foundUsers, insertedUsers);
        }
        filesHandled += 1;
    }

    private void handleFeedObject(FeedObject feedObject) {
        if (feedObject instanceof FeedTakeover feedTakeover) {
            handleTakeover(feedTakeover);
        }
    }

    private User getUpdateOrCreate(FeedUser feedUser, Instant time) {
        if (feedUser == null) {
            return null;
        }
        User user = users.get(feedUser.id());
        if (user == null || user.getTime().isBefore(time)) {
            User dbUser = userService.getUpdateOrCreate((long) feedUser.id(), feedUser.name(), time);
            users.put(feedUser.id(), dbUser);
            insertedUsers += 1;
            return dbUser;
        } else {
            foundUsers += 1;
            return user;
        }
    }

    private Zone getUpdateOrCreate(FeedZone feedZone, Instant time) {
        Zone zone = zones.get(feedZone.id());
        if (zone == null || zone.getTime().isBefore(time)) {
            Zone dbZone = zoneService.getUpdateOrCreate((long) feedZone.id(), feedZone.name(), time);
            zones.put(feedZone.id(), dbZone);
            insertedZones += 1;
            return dbZone;
        } else {
            foundZones += 1;
            return zone;
        }
    }

    void handleTakeover(FeedTakeover feedTakeover) {
        Instant time = TimeUtil.turfAPITimestampToInstant(feedTakeover.getTime());
        ZoneIdTime zoneIdTime = new ZoneIdTime(feedTakeover.getZone().id(), time);
        if (visits.contains(zoneIdTime)) {
            foundVisits += 1;
            if (feedTakeover.getZone().isTake()) {
                skippedTakes += 1;
            } else {
                skippedRevisits += 1;
            }
            FeedUser[] assists = feedTakeover.getAssists();
            if (assists != null) {
                skippedAssists += assists.length;
            }
            return;
        }

        Zone zone = getUpdateOrCreate(feedTakeover.getZone(), time);
        User user = getUpdateOrCreate(feedTakeover.getZone().currentOwner(), time);
        getUpdateOrCreate(feedTakeover.getZone().previousOwner(), time);
        VisitType type = (feedTakeover.getZone().isTake())
                ? VisitType.TAKE : VisitType.REVISIT;
        Visit existingVisit = visitService.getVisit(zone, user, time);
        FeedUser[] assists = feedTakeover.getAssists();
        if (existingVisit != null) {
            if (foundVisits >= 0) {
                logger.error("visits lacks {}, visitService contains {}", zoneIdTime, existingVisit);
                throw new RuntimeException("Cache not working!");
            }
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
        visits.add(zoneIdTime);
        insertedVisits += 1;
    }

    private void addVisit(Zone zone, User user, Instant time, VisitType type) {
        Visit visit = visitService.create(zone, user, time, type);
        logger.trace("Added visit {}", visit);
    }

    public void calculateProgress() {
        logger.error("Code missing here!");
    }

    private record ZoneIdTime(int zoneId, Instant time) {
    }
}
