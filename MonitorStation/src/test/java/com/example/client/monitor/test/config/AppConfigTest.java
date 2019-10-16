package com.example.client.monitor.test.config;

import com.example.client.monitor.config.AppConfig;
import com.example.client.monitor.domain.Observation;
import com.example.client.monitor.domain.StationPreferences;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@RunWith(SpringRunner.class)
public class AppConfigTest {

    @Autowired
    private Environment environment;

    @Value("${station.id}")
    private String stationId;

    @Value("${station.seed-water-level}")
    private double seedWaterLevel;

    @Value("${station.seed-water-flow}")
    private int seedWaterFlow;

    @Value("${station.increment-value}")
    private double incrementValue;

    @Value("${station.lat}")
    private double lat;

    @Value("${station.lon}")
    private double lon;

    @Value("${station.gateway-url:null}")
    private String gatewayUrl;

    @Autowired
    private AppConfig appConfig;

    @Test
    public void testStationPreferencesBean() {
        StationPreferences stationPreferences = appConfig.stationPreferences();
        assertThat(stationPreferences, notNullValue());
        assertThat(stationPreferences.getStationId(), equalTo(stationId));
        assertThat(stationPreferences.getName(), equalTo(stationId));
        assertThat(stationPreferences.getSeedWaterLevel(), equalTo(BigDecimal.valueOf(seedWaterLevel).setScale(2, RoundingMode.HALF_UP)));
        assertThat(stationPreferences.getSeedWaterFlow(), equalTo(seedWaterFlow));
        assertThat(stationPreferences.getIncrementValue(), equalTo(BigDecimal.valueOf(incrementValue).setScale(1, RoundingMode.HALF_UP)));
        assertThat(stationPreferences.getLat(), equalTo(BigDecimal.valueOf(lat).setScale(6, RoundingMode.HALF_UP)));
        assertThat(stationPreferences.getLon(), equalTo(BigDecimal.valueOf(lon).setScale(6, RoundingMode.HALF_UP)));
    }

    @Test
    public void testLastObservationBean() {
        Observation observation = appConfig.lastObservation();
        assertThat(observation, notNullValue());
        assertThat(observation.getStationId(), equalTo(stationId));
        assertThat(observation.getWaterLevel(), equalTo(BigDecimal.valueOf(seedWaterLevel).setScale(2, RoundingMode.HALF_UP)));
        assertThat(observation.getWaterFlow(), equalTo(seedWaterFlow));
        assertThat(observation.getLat(), equalTo(BigDecimal.valueOf(lat).setScale(6, RoundingMode.HALF_UP)));
        assertThat(observation.getLon(), equalTo(BigDecimal.valueOf(lon).setScale(6, RoundingMode.HALF_UP)));
    }

    @Test
    public void testCallbackUrl() throws UnknownHostException {
        String url = appConfig.callbackUrl();
        assertThat(url, notNullValue());
        assertThat(url, containsString("http://".concat(InetAddress.getLocalHost().getHostAddress())));
        assertThat(url, containsString("/api/vi/stationPreferences"));

    }
}
