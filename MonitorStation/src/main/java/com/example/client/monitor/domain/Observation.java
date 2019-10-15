package com.example.client.monitor.domain;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ToString(exclude = "encodedImage")
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
