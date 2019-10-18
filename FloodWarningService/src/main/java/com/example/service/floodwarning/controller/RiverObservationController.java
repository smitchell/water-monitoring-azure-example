package com.example.service.floodwarning.controller;

import com.example.service.floodwarning.domain.FloodAdvisory;
import com.example.service.floodwarning.domain.Observation;
import com.example.service.floodwarning.domain.SurfaceWaterMonitorPoint;
import com.example.service.floodwarning.event.ApplicationEvent;
import com.example.service.floodwarning.producer.FloodAdvisoryProducer;
import com.example.service.floodwarning.repository.FloodAdvisoryRepository;
import com.example.service.floodwarning.repository.ObservationRepository;
import com.example.service.floodwarning.repository.SurfaceWaterMonitorPointRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class RiverObservationController {

    private final FloodAdvisoryProducer floodAdvisoryProducer;
    private final FloodAdvisoryRepository floodAdvisoryRepository;
    private final ObservationRepository observationRepository;
    private final SurfaceWaterMonitorPointRepository surfaceWaterMonitorPointRepository;

    public RiverObservationController(
            FloodAdvisoryProducer floodAdvisoryProducer,
            FloodAdvisoryRepository floodAdvisoryRepository,
            ObservationRepository observationRepository,
            SurfaceWaterMonitorPointRepository surfaceWaterMonitorPointRepository) {
        this.floodAdvisoryProducer = floodAdvisoryProducer;
        this.floodAdvisoryRepository = floodAdvisoryRepository;
        this.observationRepository = observationRepository;
        this.surfaceWaterMonitorPointRepository = surfaceWaterMonitorPointRepository;
    }


    public void processProcessRiverObservation(ApplicationEvent riverObservationEvent) throws Exception {
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
        if (rootNode.hasNonNull("imageUrl")) {
            o.setImageUrl(rootNode.path("imageUrl").asText());
        }
        observationRepository.save(o);
        Optional<FloodAdvisory> optional = computeFloodAdvisory(o);
        if (optional.isPresent()) {
            floodAdvisoryProducer.publish(optional.get());
        }
    }

    public Optional<FloodAdvisory> computeFloodAdvisory(Observation observation) throws Exception {
        Optional<SurfaceWaterMonitorPoint> optional = surfaceWaterMonitorPointRepository.findByStationId(observation.getStationId());
        Assert.isTrue(optional.isPresent(), "Surface Water Monitor Point not found " + observation.getStationId());
        SurfaceWaterMonitorPoint surfaceWaterMonitorPoint = optional.get();
        FloodAdvisory floodAdvisory = null;
        if (surfaceWaterMonitorPoint.getFloodMajor().compareTo(observation.getWaterLevel()) < 0) {
            floodAdvisory = generateFloodAdvisory(FloodAdvisory.TYPE_MAJOR, observation, surfaceWaterMonitorPoint);
        } else if (surfaceWaterMonitorPoint.getFloodModerate().compareTo(observation.getWaterLevel()) < 0) {
            floodAdvisory = generateFloodAdvisory(FloodAdvisory.TYPE_MODERATE, observation, surfaceWaterMonitorPoint);
        } else if (surfaceWaterMonitorPoint.getFloodMinor().compareTo(observation.getWaterLevel()) < 0) {
            floodAdvisory = generateFloodAdvisory(FloodAdvisory.TYPE_MINOR, observation, surfaceWaterMonitorPoint);
        }

        // Check for an existing flood advisory
        List<FloodAdvisory> activeFloodAdvisories =
                floodAdvisoryRepository.findBySurfaceWaterMonitorPointStationIdAndFloodAdvisoryStatusOrderByAdvisoryStartTimeDesc(
                        observation.getStationId(), FloodAdvisory.STATUS_ACTIVE);
        boolean existingFloodAdvisory = false;
        if (!activeFloodAdvisories.isEmpty()) {
            for (FloodAdvisory activeFloodAdvisory : activeFloodAdvisories) {
                if (floodAdvisory != null && activeFloodAdvisory.getFloodAdvisoryType().equals(floodAdvisory.getFloodAdvisoryType())) {
                    existingFloodAdvisory = true;
                    // If the existing advisory is the same advisory type and hasn't expired, don't publish a new one.
                    activeFloodAdvisory.setAdvisoryEndTime(floodAdvisory.getAdvisoryEndTime());
                    floodAdvisoryRepository.save(activeFloodAdvisory);
                } else {
                    // If floodAdvisory is null or the existing advisory is for a different advisory type, expire it.
                    activeFloodAdvisory.setAdvisoryEndTime(new Date());
                    activeFloodAdvisory.setFloodAdvisoryStatus(FloodAdvisory.STATUS_EXPIRED);
                    floodAdvisoryRepository.save(activeFloodAdvisory);
                    if (floodAdvisory == null) {
                        log.info(String.format("Flood advisory (%S) cleared.", activeFloodAdvisory.getFloodAdvisoryType()));
                    }
                }
            }
        }
        if (existingFloodAdvisory || floodAdvisory == null) {
            return Optional.empty();
        }
        return Optional.of(floodAdvisoryRepository.save(floodAdvisory));
    }

    public FloodAdvisory generateFloodAdvisory(String floodAdvisoryType, Observation observation, SurfaceWaterMonitorPoint surfaceWaterMonitorPoint) {
        FloodAdvisory floodAdvisory = new FloodAdvisory();
        floodAdvisory.setStationId(surfaceWaterMonitorPoint.getStationId());
        floodAdvisory.setFloodAdvisoryType(floodAdvisoryType);
        floodAdvisory.setFloodAdvisoryStatus("ACTIVE");
        floodAdvisory.setAdvisoryStartTime(new Date());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 2);
        floodAdvisory.setAdvisoryEndTime(cal.getTime());
        floodAdvisory.setDescription(String.format("%s (%s ft) - Observation - %s", floodAdvisoryType, observation.getWaterLevel(), surfaceWaterMonitorPoint.getName()));
        floodAdvisory.setSurfaceWaterMonitorPoint(surfaceWaterMonitorPoint);
        return floodAdvisory;
    }

}

