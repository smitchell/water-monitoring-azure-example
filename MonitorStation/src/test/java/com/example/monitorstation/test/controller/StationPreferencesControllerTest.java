package com.example.monitorstation.test.controller;

import com.example.monitorstation.controller.StationPreferencesController;
import com.example.monitorstation.domain.StationPreferences;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

@SpringBootTest
@RunWith(SpringRunner.class)
public class StationPreferencesControllerTest {

    @Autowired
    private StationPreferences stationPreferences;

    @Autowired
    private StationPreferencesController stationPreferencesController;

    @Test
    public void testUpdateStationPreferences() {
        StationPreferences prefs = new StationPreferences();
        prefs.setStationId("testStationId");
        prefs.setName("Test Station Name");
        prefs.setGatewayUrl("http://www.gateway.com");
        prefs.setIncrementValue(BigDecimal.valueOf(1).setScale(1, RoundingMode.HALF_UP));
        prefs.setLat(BigDecimal.valueOf(39.099728D).setScale(6, RoundingMode.HALF_UP));
        prefs.setLon(BigDecimal.valueOf(-94.578568D).setScale(6, RoundingMode.HALF_UP));
        StationPreferences updatedStationPreferences = stationPreferencesController.updateStationPreferences(prefs);
        assertThat(updatedStationPreferences, notNullValue());
        StationPreferences currentPrefs = stationPreferencesController.getStationPreferences();
        assertThat(currentPrefs.getStationId(), equalTo(prefs.getStationId()));
        assertThat(currentPrefs.getName(), equalTo(prefs.getName()));
        assertThat(currentPrefs.getIncrementValue(), equalTo(prefs.getIncrementValue()));
        assertThat(currentPrefs.getLat(), equalTo(prefs.getLat()));
        assertThat(currentPrefs.getLon(), equalTo(prefs.getLon()));
    }

    @Test
    public void testGetStationPreferences() {
        StationPreferences stationPreferences = stationPreferencesController.getStationPreferences();
        assertThat(stationPreferences, notNullValue());
        assertThat(stationPreferences.getStationId(), notNullValue());
    }
}
