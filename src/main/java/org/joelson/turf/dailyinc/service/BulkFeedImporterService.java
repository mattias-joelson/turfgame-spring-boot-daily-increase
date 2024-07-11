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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
public class BulkFeedImporterService {

    private static final Logger logger = LoggerFactory.getLogger(BulkFeedImporterService.class);

    private record ZoneIdTime(int zoneId, Instant time) {
    }

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

    private User getUpdateOrCreate(org.joelson.turf.turfgame.apiv5.User userV5, Instant time) {
        if (userV5 == null) {
            return null;
        }
        /*User user = users.get(userV5.getId());
        if (user == null || user.getTime().isBefore(time)) {
            user = userService.getUpdateOrCreate((long) userV5.getId(), userV5.getName(), time);
            users.put(userV5.getId(), user);
            insertedUsers += 1;
        } else {
            foundUsers += 1;
        }
        return user;*/
        return userService.getUpdateOrCreate((long) userV5.getId(), userV5.getName(), time);
    }

    private Zone getUpdateOrCreate(org.joelson.turf.turfgame.apiv5.Zone zoneV5, Instant time) {
        /*Zone zone = zones.get(zoneV5.getId());
        if (zone == null || zone.getTime().isBefore(time)) {
            zone = zoneService.getUpdateOrCreate((long) zoneV5.getId(), zoneV5.getName(), time);
            zones.put(zoneV5.getId(), zone);
            insertedZones += 1;
        } else {
            foundZones += 1;
        }
        return zone;*/
        return zoneService.getUpdateOrCreate((long) zoneV5.getId(), zoneV5.getName(), time);
    }

    void handleTakeover(FeedTakeover feedTakeover) {
        Instant time = TimeUtil.turfAPITimestampToInstant(feedTakeover.getTime());
        ZoneIdTime zoneIdTime = new ZoneIdTime(feedTakeover.getZone().getId(), time);
        if (visits.contains(zoneIdTime)) {
            foundVisits += 1;
            int currentOwnerId = feedTakeover.getZone().getCurrentOwner().getId();
            org.joelson.turf.turfgame.apiv5.User previousOwner = feedTakeover.getZone().getPreviousOwner();
            if (previousOwner == null || currentOwnerId != previousOwner.getId()) {
                skippedTakes += 1;
            } else {
                skippedRevisits += 1;
            }
            org.joelson.turf.turfgame.apiv5.User[] assists = feedTakeover.getAssists();
            if (assists != null) {
                skippedAssists += assists.length;
            }
            return;
        }

        Zone zone = getUpdateOrCreate(feedTakeover.getZone(), time);
        User user = getUpdateOrCreate(feedTakeover.getZone().getCurrentOwner(), time);
        User previousUser = getUpdateOrCreate(feedTakeover.getZone().getPreviousOwner(), time);
        VisitType type = (previousUser == null || !Objects.equals(user.getId(), previousUser.getId()))
                ? VisitType.TAKE : VisitType.REVISIT;
        Visit existingVisit = visitService.getVisit(zone, user, time);
        org.joelson.turf.turfgame.apiv5.User[] assists = feedTakeover.getAssists();
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
}
