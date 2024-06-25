package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.Zone;
import org.joelson.turf.dailyinc.model.ZoneRepository;
import org.joelson.turf.turfgame.FeedObject;
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

@Service
public class FeedImporterService {

    Logger logger = LoggerFactory.getLogger(FeedImporterService.class);

    @Autowired
    ZoneRepository zoneRepository;

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
        if (feedObject instanceof FeedZone feedZone) {
            addZone(feedZone);
        }
    }

    private void addZone(FeedZone feedZone) {
        Zone zone = new Zone((long) feedZone.getZone().getId(), feedZone.getZone().getName(),
                TimeUtil.turfAPITimestampToInstant(feedZone.getTime()));
        zoneRepository.save(zone);
        logger.trace(String.format("Added zone %s", zone));
    }
}
