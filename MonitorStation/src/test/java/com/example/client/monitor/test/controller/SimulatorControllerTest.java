package com.example.client.monitor.test.controller;

import com.example.client.monitor.controller.SimulatorController;
import com.example.client.monitor.domain.Observation;
import com.example.client.monitor.domain.StationPreferences;
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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.number.OrderingComparison.lessThan;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SimulatorControllerTest {

    @Autowired
    private StationPreferences stationPreferences;

    @Autowired
    private Observation lastObservation;

    @Autowired
    private SimulatorController simulatorController;

    @Before
    public void runBefore() {
        stationPreferences.setStationId("testStationId");
        stationPreferences.setName("Test Station Name");
        stationPreferences.setIncrementValue(BigDecimal.valueOf(1).setScale(1, RoundingMode.HALF_UP));
        stationPreferences.setLat(BigDecimal.valueOf(39.099728D).setScale(6, RoundingMode.HALF_UP));
        stationPreferences.setLon(BigDecimal.valueOf(-94.578568D).setScale(6, RoundingMode.HALF_UP));
    }

    /**
     * On the first pass, the water level is set to the seed value passed in the station preferences
      */
    @Test
    public void testIncrementWaterLevelInitially() throws Exception {
        lastObservation.setWaterFlow(null);
        lastObservation.setWaterLevel(null);
        Observation observation = simulatorController.incrementWaterLevel();
        assertThat(observation, notNullValue());
        assertThat(observation.getTime(), notNullValue());
        assertThat(observation.getWaterFlow(), is(equalTo(stationPreferences.getSeedWaterFlow())));
        assertThat(observation.getWaterLevel(), is(equalTo(stationPreferences.getSeedWaterLevel())));
    }

    /**
     * On subsequent passes, the water level computed by incrementing the last observation by the increment amount.
     */
    @Test
    public void testIncrementWaterLevel() throws Exception {
        lastObservation.setWaterLevel(BigDecimal.valueOf(25).setScale(2, RoundingMode.HALF_UP));
        lastObservation.setWaterFlow(100);
        Observation observation = simulatorController.incrementWaterLevel();
        assertThat(observation, notNullValue());
        assertThat(observation.getTime(), notNullValue());
        assertThat(observation.getStationId(), is(equalTo(stationPreferences.getStationId())));
        assertThat(observation.getLat(), is(equalTo(stationPreferences.getLat())));
        assertThat(observation.getLon(), is(equalTo(stationPreferences.getLon())));
        assertThat(observation.getWaterFlow(), is(equalTo(stationPreferences.getSeedWaterFlow())));
        assertThat(observation.getWaterLevel(), is(equalTo(lastObservation.getWaterLevel().add(stationPreferences.getIncrementValue()).setScale(2, RoundingMode.HALF_UP))));
    }

    @Test
    public void testKeepSimulationInBounds_RiverIsLow() {
        BigDecimal startingIncrementAmount = BigDecimal.valueOf(-0.6D).setScale(2, RoundingMode.HALF_UP);
        lastObservation.setWaterLevel(stationPreferences.getSeedWaterLevel().subtract(BigDecimal.valueOf(2)));
        stationPreferences.setIncrementValue(startingIncrementAmount);
        simulatorController.keepSimulationInBounds(lastObservation);
        assertThat(stationPreferences.getIncrementValue().doubleValue(), greaterThan(0D));
    }

    @Test
    public void testKeepSimulationInBounds_RiverIsNormal() {
        BigDecimal startingIncrementAmount = BigDecimal.valueOf(0.6D).setScale(2, RoundingMode.HALF_UP);
        lastObservation.setWaterLevel(stationPreferences.getSeedWaterLevel().add(BigDecimal.valueOf(2)));
        stationPreferences.setIncrementValue(startingIncrementAmount);
        simulatorController.keepSimulationInBounds(lastObservation);
        assertThat(stationPreferences.getIncrementValue().doubleValue(), equalTo(startingIncrementAmount.doubleValue()));
    }

    @Test
    public void testKeepSimulationInBounds_RiverIsHigh() {
        BigDecimal startingIncrementAmount = BigDecimal.valueOf(0.6D).setScale(2, RoundingMode.HALF_UP);
        lastObservation.setWaterLevel(stationPreferences.getSeedWaterLevel().add(BigDecimal.valueOf(50)));
        stationPreferences.setIncrementValue(startingIncrementAmount);
        simulatorController.keepSimulationInBounds(lastObservation);
        assertThat(stationPreferences.getIncrementValue().doubleValue(), lessThan(0D));
    }
}
