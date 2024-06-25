package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.UserRepository;
import org.joelson.turf.dailyinc.model.Zone;
import org.joelson.turf.dailyinc.model.ZoneRepository;
import org.joelson.turf.turfgame.FeedObject;
import org.joelson.turf.turfgame.apiv5.FeedChat;
import org.joelson.turf.turfgame.apiv5.FeedMedal;
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

@Service
public class FeedImporterService {

    Logger logger = LoggerFactory.getLogger(FeedImporterService.class);

    @Autowired
    UserRepository userRepository;

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
            addZone((long) feedZone.getZone().getId(), feedZone.getZone().getName(),
                    TimeUtil.turfAPITimestampToInstant(feedZone.getTime()));
        } else if (feedObject instanceof FeedChat feedChat) {
            addUser((long) feedChat.getSender().getId(), feedChat.getSender().getName(),
                    TimeUtil.turfAPITimestampToInstant(feedChat.getTime()));
        } else if (feedObject instanceof FeedMedal feedMedal) {
            addUser((long) feedMedal.getUser().getId(), feedMedal.getUser().getName(),
                    TimeUtil.turfAPITimestampToInstant(feedMedal.getTime()));
        }
    }

    private void addUser(Long id, String name, Instant instant) {
        User user = new User(id, name, instant);
        userRepository.save(user);
        logger.trace(String.format("Added user %s", user));
    }

    private void addZone(Long id, String nane, Instant instant) {
        Zone zone = new Zone(id, nane, instant);
        zoneRepository.save(zone);
        logger.trace(String.format("Added zone %s", zone));
    }
}
