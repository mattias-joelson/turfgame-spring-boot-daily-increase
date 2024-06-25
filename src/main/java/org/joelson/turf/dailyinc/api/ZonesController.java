package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.Zone;
import org.joelson.turf.dailyinc.service.ZoneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/zones")
public class ZonesController {

    Logger logger = LoggerFactory.getLogger(ZonesController.class);

    @Autowired
    ZoneService zoneService;

    private static Long toLong(String identifier) {
        try {
            Long id = Long.parseLong(identifier);
            return (String.valueOf(id).equals(identifier)) ? id : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @GetMapping("/")
    public List<Zone> getZones() {
        logger.trace("getZones()");
        return zoneService.getZones().stream().sorted(Comparator.comparing(Zone::getId)).toList();
    }

    @GetMapping("/{identifier}")
    public Object getZoneByIdentifier(@PathVariable String identifier) {
        logger.trace(String.format("getZoneByIdentifier(%s)", identifier));
        Zone zone;
        Long id = toLong(identifier);
        if (id != null) {
            zone = zoneService.getZoneById(id);
        } else {
            zone = zoneService.getZoneByName(identifier);
        }
        if (zone != null) {
            return zone;
        }
        return new JsonError("/errors/invalid-zone-identifier", "Incorrect zone identifier", 404, "", "/api/zones/" + identifier);
    }
}
