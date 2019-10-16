package com.example.client.monitor.controller;

import com.example.client.monitor.domain.Observation;
import com.example.client.monitor.domain.StationPreferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class StationPreferencesController {

    private final StationPreferences stationPreferences;

    @Autowired
    public StationPreferencesController(
            final StationPreferences stationPreferences,
            final Observation lastObservation) {
        this.stationPreferences = stationPreferences;
    }

    @PutMapping("/stationPreferences")
    public StationPreferences updateStationPreferences(@RequestBody final StationPreferences updates) {
        if (updates.getStationId() != null) {
            stationPreferences.setStationId(updates.getStationId());
        }
        if (updates.getName() != null) {
            stationPreferences.setName(updates.getName());
        }
        if (updates.getIncrementValue() != null) {
            stationPreferences.setIncrementValue(updates.getIncrementValue());
        }
        if (updates.getIncrementValue() != null) {
            stationPreferences.setIncrementValue(updates.getIncrementValue());
        }
        if (updates.getLat() != null) {
            stationPreferences.setLat(updates.getLat());
        }
        if (updates.getLon() != null) {
            stationPreferences.setLon(updates.getLon());
        }
        return stationPreferences;
    }

    @GetMapping("/stationPreferences")
    public StationPreferences getStationPreferences() {
        return this.stationPreferences;
    }
}
