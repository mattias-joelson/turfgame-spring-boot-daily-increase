package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.projection.UserIdAndNameVisit;
import org.joelson.turf.dailyinc.projection.ZoneIdAndName;
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

import java.util.List;

@RestController
@RequestMapping("/api/zones")
public class ZonesController {

    private final Logger logger = LoggerFactory.getLogger(ZonesController.class);

    @Autowired
    VisitService visitService;

    @Autowired
    ZoneService zoneService;

    @GetMapping("")
    public List<ZoneIdAndName> getZones() {
        logger.trace("getZones()");
        return zoneService.getSortedZones(ZoneIdAndName.class);
    }

    @GetMapping({ "/", "/{zoneId}" })
    public ResponseEntity<ZoneIdAndName> getZoneByIdentifier(@PathVariable(required = false) String zoneId) {
        logger.trace(String.format("getZoneByIdentifier(%s)", zoneId));
        ZoneIdAndName zone = lookupZoneIdAndNameByIdentifier(zoneId);
        if (zone == null) {
            return ControllerUtil.respondNotFound();
        }
        return ControllerUtil.respondOk(zone);
    }

    @GetMapping({ "//visits", "/{zoneId}/visits" })
    public ResponseEntity<List<UserIdAndNameVisit>> getZoneVisitsByIdentifier(
            @PathVariable(required = false) String zoneId) {
        logger.trace(String.format("getZoneVisitsByIdentifier(%s)", zoneId));
        ZoneIdAndName zone = lookupZoneIdAndNameByIdentifier(zoneId);
        if (zone == null) {
            return ControllerUtil.respondNotFound();
        }
        return ControllerUtil.respondOk(visitService.getSortedVisitsByZone(zone.getId(), UserIdAndNameVisit.class));
    }

    private ZoneIdAndName lookupZoneIdAndNameByIdentifier(String identifier) {
        Long id = ControllerUtil.toLong(identifier);
        if (id != null) {
            return zoneService.getZoneById(id, ZoneIdAndName.class);
        } else {
            return zoneService.getZoneByName(identifier, ZoneIdAndName.class);
        }
    }
}
