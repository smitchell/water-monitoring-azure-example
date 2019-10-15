package com.example.service.floodwarning.controller;

import com.example.service.floodwarning.domain.*;
import com.example.service.floodwarning.repository.FloodAdvisoryRepository;
import com.example.service.floodwarning.repository.ObservationRepository;
import com.example.service.floodwarning.repository.SurfaceWaterMonitorPointRepository;
import com.example.service.floodwarning.service.StorageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import java.util.Base64;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Controller
public class RiverObservationController {

    private FloodAdvisoryRepository floodAdvisoryRepository;
    private ObservationRepository observationRepository;
    private SurfaceWaterMonitorPointRepository surfaceWaterMonitorPointRepository;
    private StorageService storageService;

    @Autowired
    public RiverObservationController(
            FloodAdvisoryRepository floodAdvisoryRepository,
            ObservationRepository observationRepository,
            SurfaceWaterMonitorPointRepository surfaceWaterMonitorPointRepository,
            StorageService storageService) {
        this.floodAdvisoryRepository = floodAdvisoryRepository;
        this.observationRepository = observationRepository;
        this.surfaceWaterMonitorPointRepository = surfaceWaterMonitorPointRepository;
        this.storageService = storageService;
    }

    public void processProcessRiverObservation(RiverObservationEvent riverObservationEvent) throws Exception {
        Observation o = new Observation();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(riverObservationEvent.getData());
        if (rootNode.hasNonNull("stationId")) {
            o.setStationId(rootNode.path("stationId").asText());
        }
        if (rootNode.hasNonNull("time")) {
            o.setTime(new Date(rootNode.path("time").asLong()));
        }
        if (rootNode.hasNonNull("waterLevel")) {
            o.setWaterLevel(BigDecimal.valueOf(rootNode.path("waterLevel").asDouble()).setScale(2, RoundingMode.HALF_UP));
        }
        if (rootNode.hasNonNull("waterFlow")) {
            o.setWaterFlow(rootNode.path("waterFlow").asInt());
        }
        if (rootNode.hasNonNull("lat")) {
            o.setLat(BigDecimal.valueOf(rootNode.path("lat").asDouble()).setScale(6, RoundingMode.HALF_UP));
        }
        if (rootNode.hasNonNull("lon")) {
            o.setLon(BigDecimal.valueOf(rootNode.path("lon").asDouble()).setScale(6, RoundingMode.HALF_UP));
        }
        if (rootNode.hasNonNull("encodedImage")) {
            String encodedImage = rootNode.path("encodedImage").asText();
            if (rootNode.hasNonNull("imageExtension")) {
                String imageExtension = rootNode.path("imageExtension").asText();
                byte[] bytes = Base64.getDecoder().decode(encodedImage);
                o.setImageUrl(storageService.writeBlobFile(o.getStationId(), bytes, imageExtension));
                log.info("Photo created: " + o.getImageUrl());
            }
        }
        observationRepository.save(o);
        Optional<FloodAdvisory> optional = computeFloodAdvisory(o);
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
        floodAdvisory.setDescription(String.format("%s (%s ft) - Observation - %s", floodAdvisoryType.name(), observation.getWaterLevel(), surfaceWaterMonitorPoint.getName()));
        floodAdvisory.setSurfaceWaterMonitorPoint(surfaceWaterMonitorPoint);
        return floodAdvisory;
    }
}

