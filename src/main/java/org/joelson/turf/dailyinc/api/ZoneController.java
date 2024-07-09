package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.projection.UserIdAndNameVisit;
import org.joelson.turf.dailyinc.projection.ZoneIdAndName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
public class ZoneController {

    private static final String ZONES_RANGE_UNIT = "zones";
    private static final Logger logger = LoggerFactory.getLogger(ZoneController.class);

    @Autowired
    VisitAPIService visitAPIService;

    @Autowired
    ZoneAPIService zoneAPIService;

    @GetMapping("")
    public ResponseEntity<List<ZoneIdAndName>> getZones(
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String range) {
        logger.trace("getZones()");
        if (range == null) {
            return RangeRequestUtil.handleIdRequest(ZONES_RANGE_UNIT, ZoneIdAndName.class,
                    zoneAPIService::getSortedZonesBetween, ZoneIdAndName::getId);
        } else {
            return RangeRequestUtil.handleIdRequest(ZONES_RANGE_UNIT, range, ZoneIdAndName.class,
                    zoneAPIService::getSortedZonesBetween, zoneAPIService::getLastSortedZones, ZoneIdAndName::getId);
        }
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
            @PathVariable(required = false) String zoneId,
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String range) {
        logger.trace(String.format("getZoneVisitsByIdentifier(%s)", zoneId));
        ZoneIdAndName zone = lookupZoneIdAndNameByIdentifier(zoneId);
        if (zone == null) {
            return ControllerUtil.respondNotFound();
        }
        if (range == null) {
            return RangeRequestUtil.handleRequest(VisitController.VISITS_RANGE_UNIT, UserIdAndNameVisit.class,
                    (firstRow, lastRow, type) -> visitAPIService.getSortedVisitsByZone(zone.getId(), firstRow, lastRow,
                            type));
        } else {
            return RangeRequestUtil.handleRequest(VisitController.VISITS_RANGE_UNIT, range, UserIdAndNameVisit.class,
                    (firstRow, lastRow, type) -> visitAPIService.getSortedVisitsByZone(zone.getId(), firstRow, lastRow,
                            type),
                    (rows, type) -> visitAPIService.getLastSortedVisitsByZone(zone.getId(), rows, type));
        }
    }

    private ZoneIdAndName lookupZoneIdAndNameByIdentifier(String identifier) {
        Long id = ControllerUtil.toLong(identifier);
        if (id != null) {
            return zoneAPIService.getZoneById(id, ZoneIdAndName.class);
        } else {
            return zoneAPIService.getZoneByName(identifier, ZoneIdAndName.class);
        }
    }
}
