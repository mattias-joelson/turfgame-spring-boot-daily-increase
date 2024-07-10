package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZoneAPIService {

    @Autowired
    ZoneRepository zoneRepository;

    public <T> List<T> getSortedBetween(Long minId, Long maxId, Class<T> type) {
        return zoneRepository.findSortedBetween(minId, maxId, 100, type);
    }

    public <T> List<T> getLastSorted(int last, Class<T> type) {
        return zoneRepository.findLastSortedReversed(Math.min(last, 100), type).reversed();
    }

    public <T> T getZoneById(Long id, Class<T> type) {
        return zoneRepository.findById(id, type).orElse(null);
    }

    public <T> T getZoneByName(String name, Class<T> type) {
        return zoneRepository.findByName(name, type).orElse(null);
    }
}
