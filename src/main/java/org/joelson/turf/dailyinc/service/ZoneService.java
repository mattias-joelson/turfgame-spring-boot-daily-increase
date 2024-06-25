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

    public List<Zone> getZones() {
        return zoneRepository.findAll();
    }

    public Zone getZoneById(Long id) {
        return zoneRepository.findById(id).orElse(null);
    }

    public Zone getZoneByName(String name) {
        return zoneRepository.findByName(name).orElse(null);
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
