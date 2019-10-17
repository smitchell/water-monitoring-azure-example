package com.example.client.monitor.config;

import com.example.client.monitor.domain.Observation;
import com.example.client.monitor.domain.StationPreferences;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Configuration
public class AppConfig {

    private final String stationId;
    private final int seedWaterFlow;
    private final double seedWaterLevel;
    private final double incrementValue;
    private final double maxWaterLevel;
    private final double lat;
    private final double lon;

    @Autowired
    public AppConfig(
            @Value("${station.id}") final String stationId,
            @Value("${station.seed-water-level}") final double seedWaterLevel,
            @Value("${station.seed-water-flow}") final int seedWaterFlow,
            @Value("${station.increment-value}") final double incrementValue,
            @Value("${station.max-water-level}") final double maxWaterLevel,
            @Value("${station.lat}") final double lat,
            @Value("${station.lon}") final double lon) {
        this.stationId = stationId;
        this.seedWaterLevel = seedWaterLevel;
        this.seedWaterFlow = seedWaterFlow;
        this.incrementValue = incrementValue;
        this.maxWaterLevel = maxWaterLevel;
        this.lat = lat;
        this.lon = lon;
    }

    @PostConstruct
    public void postConstruct() {
        Assert.notNull(stationPreferences(), "Station preferences must not be null");
        Assert.notNull(lastObservation(), "Last Observation must not be null");
        stationPreferences().setStationId(stationId);
        stationPreferences().setName(stationId);
        stationPreferences().setSeedWaterLevel(BigDecimal.valueOf(seedWaterLevel).setScale(2, RoundingMode.HALF_UP));
        stationPreferences().setSeedWaterFlow(seedWaterFlow);
        stationPreferences().setIncrementValue(BigDecimal.valueOf(incrementValue).setScale(1, RoundingMode.HALF_UP));
        stationPreferences().setMaxWaterLevel(BigDecimal.valueOf(maxWaterLevel).setScale(2, RoundingMode.HALF_UP));
        stationPreferences().setLat(BigDecimal.valueOf(lat).setScale(6, RoundingMode.HALF_UP));
        stationPreferences().setLon(BigDecimal.valueOf(lon).setScale(6, RoundingMode.HALF_UP));
        lastObservation().setStationId(stationId);
        lastObservation().setWaterLevel(BigDecimal.valueOf(seedWaterLevel).setScale(2, RoundingMode.HALF_UP));
        lastObservation().setWaterFlow(seedWaterFlow);
        lastObservation().setLat(BigDecimal.valueOf(lat).setScale(6, RoundingMode.HALF_UP));
        lastObservation().setLon(BigDecimal.valueOf(lon).setScale(6, RoundingMode.HALF_UP));
    }

    @Bean
    public StationPreferences stationPreferences() {
        return new StationPreferences();
    }

    @Bean
    public Observation lastObservation() {
        return new Observation();
    }
}
