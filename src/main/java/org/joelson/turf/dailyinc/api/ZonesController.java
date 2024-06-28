package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.Visit;
import org.joelson.turf.dailyinc.model.Zone;
import org.joelson.turf.dailyinc.service.VisitService;
import org.joelson.turf.dailyinc.service.ZoneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    VisitService visitService;

    @Autowired
    ZoneService zoneService;

    @GetMapping("")
    public List<Zone> getZones() {
        logger.trace("getZones()");
        return zoneService.getZones().stream().sorted(Comparator.comparing(Zone::getId)).toList();
    }

    @GetMapping({ "/", "/{zoneId}" })
    public ResponseEntity<Zone> getZoneByIdentifier(@PathVariable(required = false) String zoneId) {
        logger.trace(String.format("getZoneByIdentifier(%s)", zoneId));
        Zone zone = lookupZoneByIdentifier(zoneId);
        if (zone == null) {
            return ControllerUtil.respondNotFound();
        }
        return ControllerUtil.respondOk(zone);
    }

    @GetMapping({ "//visits", "/{zoneId}/visits" })
    public ResponseEntity<List<Visit>> getZoneVisitsByIdentifier(@PathVariable(required = false) String zoneId) {
        logger.trace(String.format("getZoneVisitsByIdentifier(%s)", zoneId));
        Zone zone = lookupZoneByIdentifier(zoneId);
        if (zone == null) {
            return ControllerUtil.respondNotFound();
        }
        return ControllerUtil.respondOk(visitService.getSortedVisitsByZone(zone));
    }

    private Zone lookupZoneByIdentifier(String identifier) {
        Long id = ControllerUtil.toLong(identifier);
        if (id != null) {
            return zoneService.getZoneById(id);
        } else {
            return zoneService.getZoneByName(identifier);
        }
    }
}
