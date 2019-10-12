package com.example.client.monitor.controller;

import com.example.client.monitor.domain.Observation;
import com.example.client.monitor.domain.StationPreferences;
import com.example.client.monitor.producer.RiverObservationProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Date;

@Slf4j
@Component
public class Simulator {

    private final StationPreferences stationPreferences;
    private final Observation lastObservation;
    private final RiverObservationProducer riverObservationProducer;

    @Autowired
    public Simulator(
            final StationPreferences stationPreferences,
            final Observation lastObservation,
            RiverObservationProducer riverObservationProducer
    ) {
        this.stationPreferences = stationPreferences;
        this.lastObservation = lastObservation;
        this.riverObservationProducer = riverObservationProducer;
    }

    /**
     * Use initial detail so as not to interfere with tests
     */
    @Scheduled(fixedRate = 5000, initialDelay = 10000)
    public void simulateMeasurement() throws Exception {
        BeanUtils.copyProperties(incrementWaterLevel(), lastObservation);
        riverObservationProducer.publish(lastObservation);
    }



    public Observation incrementWaterLevel() {
        Assert.notNull(stationPreferences, "Station preferences must not be null");
        Assert.notNull(lastObservation, "Last Observation must not be null");
        Observation observation = new Observation();
        observation.setTime(new Date());
        observation.setStationId(stationPreferences.getStationId());
        observation.setLat(stationPreferences.getLat());
        observation.setLon(stationPreferences.getLon());
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
        return observation;
    }
}
