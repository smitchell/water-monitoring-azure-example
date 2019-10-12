package com.example.monitorstation.test;

import com.example.monitorstation.domain.Observation;
import com.example.monitorstation.domain.StationPreferences;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Autowired
	private Observation lastObservation;

	@Autowired
	private StationPreferences stationPreferences;

	@Test
	public void contextLoads() {
		MatcherAssert.assertThat(lastObservation, notNullValue());
		MatcherAssert.assertThat(stationPreferences, notNullValue());
		MatcherAssert.assertThat(stationPreferences.getStationId(), notNullValue());
		MatcherAssert.assertThat(stationPreferences.getSeedWaterFlow(), notNullValue());
		MatcherAssert.assertThat(stationPreferences.getSeedWaterLevel(), notNullValue());
	}

}
