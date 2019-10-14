package com.example.service.floodwarning.controller;

import com.example.service.floodwarning.domain.*;
import com.example.service.floodwarning.repository.FloodAdvisoryRepository;
import com.example.service.floodwarning.repository.ObservationRepository;
import com.example.service.floodwarning.repository.SurfaceWaterMonitorPointRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Optional;

@Service
public class RiverObservationController {

    private FloodAdvisoryRepository floodAdvisoryRepository;

    private ObservationRepository observationRepository;

    private SurfaceWaterMonitorPointRepository surfaceWaterMonitorPointRepository;

    @Autowired
    public RiverObservationController(
            FloodAdvisoryRepository floodAdvisoryRepository,
            ObservationRepository observationRepository,
            SurfaceWaterMonitorPointRepository surfaceWaterMonitorPointRepository) {
        this.floodAdvisoryRepository = floodAdvisoryRepository;
        this.observationRepository = observationRepository;
        this.surfaceWaterMonitorPointRepository = surfaceWaterMonitorPointRepository;
    }

    public void processProcessRiverObservation(RiverObservationEvent riverObservationEvent) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Observation observation = objectMapper.readValue(riverObservationEvent.getData(), Observation.class);
        observationRepository.save(observation);
        Optional<FloodAdvisory> optional = computeFloodAdvisory(observation);
    }

    public Optional<FloodAdvisory> computeFloodAdvisory(Observation observation) {
        Optional<SurfaceWaterMonitorPoint> optional = surfaceWaterMonitorPointRepository.findByStationId(observation.getStationId());
        Assert.isTrue(optional.isPresent(), "Surface Water Monitor Point not found " + observation.getStationId());
        SurfaceWaterMonitorPoint surfaceWaterMonitorPoint = optional.get();
        FloodAdvisory floodAdvisory = null;
        if (surfaceWaterMonitorPoint.getFloodMajor().compareTo(observation.getWaterLevel()) < 0) {
            floodAdvisory = generateFloodAdvisory(FloodAdvisoryType.MAJOR, observation, surfaceWaterMonitorPoint);
        } else if (surfaceWaterMonitorPoint.getFloodModerate().compareTo(observation.getWaterLevel()) < 0) {
            floodAdvisory = generateFloodAdvisory(FloodAdvisoryType.MODERATE, observation, surfaceWaterMonitorPoint);
        } else if (surfaceWaterMonitorPoint.getFloodMinor().compareTo(observation.getWaterLevel()) < 0) {
            floodAdvisory = generateFloodAdvisory(FloodAdvisoryType.MINOR, observation, surfaceWaterMonitorPoint);
        }
        if (floodAdvisory == null) {
            return Optional.empty();
        } else {
            floodAdvisoryRepository.save(floodAdvisory);
        }
        return Optional.of(floodAdvisory);
    }

    public FloodAdvisory generateFloodAdvisory(FloodAdvisoryType floodAdvisoryType, Observation observation, SurfaceWaterMonitorPoint surfaceWaterMonitorPoint) {
        FloodAdvisory floodAdvisory = new FloodAdvisory();
        floodAdvisory.setFloodAdvisoryType(floodAdvisoryType);
        Calendar cal = Calendar.getInstance();
        floodAdvisory.setAdvisoryEndTime(cal.getTime());
        cal.add(Calendar.HOUR, 2);
        floodAdvisory.setAdvisoryEndTime(cal.getTime());
        floodAdvisory.setDescription(String.format("A %s flood advisory has been issued for %s", floodAdvisoryType.name(), surfaceWaterMonitorPoint.getName()));
        floodAdvisory.setSurfaceWaterMonitorPoint(surfaceWaterMonitorPoint);
        return floodAdvisory;
    }
}

