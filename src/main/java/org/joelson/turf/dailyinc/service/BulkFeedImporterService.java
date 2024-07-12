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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final Cache<Integer, User> userCache = new Cache<>();
    private final Cache<Integer, Zone> zoneCache = new Cache<>();
    private final Cache<ZoneIdTime, ZoneIdTime> visitCache = new Cache<>();

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
        paths.sort(Comparator.reverseOrder());
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
            logger.info("Reading path {} (imported takes={}, revisits={}, assists={}, skipped takes={}, revisits={}, assists={}, visitCache={}, zoneCache={}, userCache={})",
                    path, importedTakes, importedRevisits, importedAssists, skippedTakes, skippedRevisits, skippedAssists, visitCache, zoneCache, userCache);
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
        User user = userCache.get(feedUser.id());
        if (user == null || user.getTime().isBefore(time)) {
            User dbUser = userService.getUpdateOrCreate((long) feedUser.id(), feedUser.name(), time);
            userCache.put(feedUser.id(), dbUser);
            return dbUser;
        } else {
            return user;
        }
    }

    private Zone getUpdateOrCreate(FeedZone feedZone, Instant time) {
        Zone zone = zoneCache.get(feedZone.id());
        if (zone == null || zone.getTime().isBefore(time)) {
            Zone dbZone = zoneService.getUpdateOrCreate((long) feedZone.id(), feedZone.name(), time);
            zoneCache.put(feedZone.id(), dbZone);
            return dbZone;
        } else {
            return zone;
        }
    }

    void handleTakeover(FeedTakeover feedTakeover) {
        Instant time = TimeUtil.turfAPITimestampToInstant(feedTakeover.getTime());
        ZoneIdTime zoneIdTime = new ZoneIdTime(feedTakeover.getZone().id(), time);
        if (visitCache.get(zoneIdTime) != null) {
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
            logger.trace("Skipping existing visit {}...", existingVisit);
            if (type == VisitType.TAKE) {
                skippedTakes += 1;
            } else {
                skippedRevisits += 1;
            }
            if (assists != null) {
                skippedAssists += assists.length;
            }
            visitCache.put(zoneIdTime, zoneIdTime);
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
        visitCache.put(zoneIdTime, zoneIdTime);
    }

    private void addVisit(Zone zone, User user, Instant time, VisitType type) {
        Visit visit = visitService.create(zone, user, time, type);
        logger.trace("Added visit {}", visit);
    }

    public void calculateProgress() {
        logger.error("Code missing here!");
    }

    private static class Cache<K, V> {

        private final Map<K, V> cache = new HashMap<>();
        private int found = 0;
        private int lacked = 0;
        private int inserted = 0;
        private int updated = 0;

        public V get(K key) {
            V value = cache.get(key);
            if (value != null) {
                found += 1;
            } else {
                lacked += 1;
            }
            return value;
        }

        public void put(K key, V value) {
            if (cache.containsKey(key)) {
                updated += 1;
            } else {
                inserted += 1;
            }
            cache.put(key, value);
        }

        @Override
        public String toString() {
            return String.format("Cache[size=%d, found=%d, lacked=%d, inserted=%d, updated=%s]", cache.size(), found,
                    lacked, inserted, updated);
        }
    }

    private record ZoneIdTime(int zoneId, Instant time) {
    }
}
