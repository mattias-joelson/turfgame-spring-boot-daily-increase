package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.Zone;
import org.joelson.turf.dailyinc.model.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ZoneService {

    @Autowired
    ZoneRepository zoneRepository;

    public <T> List<T> getSortedZones(Class<T> type) {
        return zoneRepository.findAllSorted(type);
    }

    public <T> T getZoneById(Long id, Class<T> type) {
        return zoneRepository.findById(id, type).orElse(null);
    }

    public <T> T getZoneByName(String name, Class<T> type) {
        return zoneRepository.findByName(name, type).orElse(null);
    }

    public Zone getUpdateOrCreate(Long id, String name, Instant time) {
        Zone zone = zoneRepository.findById(id).orElse(null);
        if (zone == null) {
            return zoneRepository.save(new Zone(id, name, time));
        } else if (time.isAfter(zone.getTime())) {
            if (!zone.getName().equals(name)) {
                zone.setName(name);
            }
            zone.setTime(time);
            return zoneRepository.save(zone);
        }
        return zone;
    }
}
