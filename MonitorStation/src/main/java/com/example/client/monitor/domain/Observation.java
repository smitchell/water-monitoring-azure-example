package com.example.client.monitor.domain;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Observation implements Serializable {
    private Date time;
    private String stationId;
    private BigDecimal waterLevel;
    private Integer waterFlow;
    private BigDecimal lat;
    private BigDecimal lon;
    private String encodedImage;
    private String imageExtension;
}
