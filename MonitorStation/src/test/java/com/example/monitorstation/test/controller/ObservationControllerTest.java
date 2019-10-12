package com.example.monitorstation.test.controller;

import com.example.monitorstation.controller.ObservationController;
import com.example.monitorstation.domain.Observation;
import com.example.monitorstation.domain.StationPreferences;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ObservationControllerTest {

    @Autowired
    private StationPreferences stationPreferences;

    @Autowired
    private ObservationController observationController;

    @Test
    public void testGetStationPreferences() {
        Observation observation = observationController.getLastObservation();
        assertThat(observation, notNullValue());
        assertThat(observation.getStationId(), notNullValue());
        assertThat(observation.getStationId(), equalTo(stationPreferences.getStationId()));
    }
}
