package com.example.client.monitor.controller;

import com.example.client.monitor.domain.Observation;
import com.example.client.monitor.domain.StationPreferences;
import com.example.client.monitor.producer.RiverObservationProducer;
import com.example.client.monitor.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@Slf4j
@Controller
public class SimulatorController {

    private final StationPreferences stationPreferences;
    private final Observation lastObservation;
    private final RiverObservationProducer riverObservationProducer;
    private final StorageService storageService;

    @Autowired
    public SimulatorController(
            final StationPreferences stationPreferences,
            final Observation lastObservation,
            final RiverObservationProducer riverObservationProducer,
            final StorageService storageService
    ) {
        this.stationPreferences = stationPreferences;
        this.lastObservation = lastObservation;
        this.riverObservationProducer = riverObservationProducer;
        this.storageService = storageService;
    }

    /**
     * Use initial detail so as not to interfere with tests
     */
    @Scheduled(fixedRate = 2000, initialDelay = 10000)
    public void simulateMeasurement() throws Exception {
        BeanUtils.copyProperties(incrementWaterLevel(), lastObservation);
        riverObservationProducer.publish(lastObservation);
    }

    public Observation incrementWaterLevel() throws Exception {
        Assert.notNull(stationPreferences, "Station preferences must not be null");
        Assert.notNull(lastObservation, "Last Observation must not be null");
        Observation observation = new Observation();
        observation.setTime(new Date());
        observation.setStationId(stationPreferences.getStationId());
        observation.setLat(stationPreferences.getLat());
        observation.setLon(stationPreferences.getLon());
        byte[] bytes = loadImageBytes("/R-1-downstream.jpg");
        String url = storageService.writeBlobFile(observation.getStationId(), bytes, "jpg");
        observation.setImageUrl(url);
        if (lastObservation.getWaterLevel() == null) {
            observation.setWaterLevel(stationPreferences.getSeedWaterLevel());
        } else {
            Assert.notNull(stationPreferences.getIncrementValue(), "Last stationPreferences increment value must not be null");
            observation.setWaterLevel(lastObservation.getWaterLevel().add(stationPreferences.getIncrementValue()));
        }
        if (lastObservation.getWaterFlow() == null) {
            observation.setWaterFlow(stationPreferences.getSeedWaterFlow());
        } else {
            observation.setWaterFlow(lastObservation.getWaterFlow());
        }
        keepSimulationInBounds(observation);
        return observation;
    }

    /**
     * If the river level falls below the initial seed level or rises above the maximum level,
     * negate the increment value to keep the simulation in bounds.
     * @param observation - The observation to be compared to bounds.
     */
    public void keepSimulationInBounds(Observation observation) {
        if (observation.getWaterLevel().compareTo(stationPreferences.getSeedWaterLevel()) < 0
                || observation.getWaterLevel().compareTo(stationPreferences.getMaxWaterLevel()) > 0) {
            stationPreferences.setIncrementValue(stationPreferences.getIncrementValue().negate());
        }
    }

    private byte[] loadImageBytes(String photoPath) {
        try {
            InputStream in = getClass()
                    .getResourceAsStream(photoPath);
            return IOUtils.toByteArray(in);
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }
}
