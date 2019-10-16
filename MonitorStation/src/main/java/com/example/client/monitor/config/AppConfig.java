package com.example.client.monitor.config;

import com.example.client.monitor.domain.Observation;
import com.example.client.monitor.domain.StationPreferences;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@Configuration
public class AppConfig {

    private final String stationId;
    private final int seedWaterFlow;
    private final double seedWaterLevel;
    private final double incrementValue;
    private final String gatewayUrl;
    private final double lat;
    private final double lon;
    private final Environment environment;

    @Autowired
    public AppConfig(
            @Value("${station.id}") final String stationId,
            @Value("${station.seed-water-level}") final double seedWaterLevel,
            @Value("${station.seed-water-flow}") final int seedWaterFlow,
            @Value("${station.increment-value}") final double incrementValue,
            @Value("${station.lat}") final double lat,
            @Value("${station.lon}") final double lon,
            @Value("${station.gateway-url:null}") final String gatewayUrl,
            Environment environment
    ) {
        this.stationId = stationId;
        this.seedWaterLevel = seedWaterLevel;
        this.seedWaterFlow = seedWaterFlow;
        this.incrementValue = incrementValue;
        this.gatewayUrl = gatewayUrl;
        this.lat = lat;
        this.lon = lon;
        this.environment = environment;
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

    @Bean
    public String callbackUrl() {
        String callbackUrl = null;
        try {
            if( callbackUrl == null ) {
                // Port
                String port = environment.getProperty("server.port");
                // Local address
                String hostAddress = InetAddress.getLocalHost().getHostAddress();
//                String hostname = InetAddress.getLocalHost().getHostName();
                callbackUrl = "http://";
                if (!"80".equals(port) && !"0".equals(port)) {
                    callbackUrl = callbackUrl.concat(":").concat(port).concat("/");
                }
                callbackUrl = callbackUrl.concat(hostAddress).concat("/api/vi/stationPreferences");
            }
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
            callbackUrl = "error";
        }
        return callbackUrl;
    }
}
