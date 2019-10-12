package com.example.monitorstation.test.controller;

import com.example.monitorstation.controller.Simulator;
import com.example.monitorstation.domain.Observation;
import com.example.monitorstation.domain.StationPreferences;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SimulatorTest {

    @Autowired
    private StationPreferences stationPreferences;

    @Autowired
    private Observation lastObservation;

    @Autowired
    private Simulator simulator;

    @Before
    public void runBefore() {
        stationPreferences.setStationId("testStationId");
        stationPreferences.setName("Test Station Name");
        stationPreferences.setGatewayUrl("http://www.gateway.com");
        stationPreferences.setIncrementValue(BigDecimal.valueOf(1).setScale(1, RoundingMode.HALF_UP));
        stationPreferences.setLat(BigDecimal.valueOf(39.099728D).setScale(6, RoundingMode.HALF_UP));
        stationPreferences.setLon(BigDecimal.valueOf(-94.578568D).setScale(6, RoundingMode.HALF_UP));
    }

    /**
     * On the first pass, the water level is set to the seed value passed in the station preferences
      */
    @Test
    public void testIncrementWaterLevelInitially() {
        lastObservation.setWaterFlow(null);
        lastObservation.setWaterLevel(null);
        Observation observation = simulator.incrementWaterLevel();
        assertThat(observation, notNullValue());
        assertThat(observation.getTime(), notNullValue());
        assertThat(observation.getWaterFlow(), is(equalTo(stationPreferences.getSeedWaterFlow())));
        assertThat(observation.getWaterLevel(), is(equalTo(stationPreferences.getSeedWaterLevel())));
    }

    /**
     * On subsequent passes, the water level computed by incrementing the last observation by the increment amount.
     */
    @Test
    public void testIncrementWaterLevel() {
        lastObservation.setWaterLevel(BigDecimal.valueOf(25).setScale(2, RoundingMode.HALF_UP));
        lastObservation.setWaterFlow(100);
        Observation observation = simulator.incrementWaterLevel();
        assertThat(observation, notNullValue());
        assertThat(observation.getTime(), notNullValue());
        assertThat(observation.getStationId(), is(equalTo(stationPreferences.getStationId())));
        assertThat(observation.getLat(), is(equalTo(stationPreferences.getLat())));
        assertThat(observation.getLon(), is(equalTo(stationPreferences.getLon())));
        assertThat(observation.getWaterFlow(), is(equalTo(stationPreferences.getSeedWaterFlow())));
        assertThat(observation.getWaterLevel(), is(equalTo(lastObservation.getWaterLevel().add(stationPreferences.getIncrementValue()).setScale(2, RoundingMode.HALF_UP))));
    }
}
