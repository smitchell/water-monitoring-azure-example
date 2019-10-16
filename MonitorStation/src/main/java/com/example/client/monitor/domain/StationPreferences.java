package com.example.client.monitor.domain;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class StationPreferences implements Serializable {

    private String stationId;
    private String name;
    private BigDecimal seedWaterLevel;
    private Integer seedWaterFlow;
    private BigDecimal incrementValue;
    private BigDecimal lat;
    private BigDecimal lon;

}
