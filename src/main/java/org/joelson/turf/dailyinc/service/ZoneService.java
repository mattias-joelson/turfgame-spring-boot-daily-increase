package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.Zone;
import org.joelson.turf.dailyinc.model.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
