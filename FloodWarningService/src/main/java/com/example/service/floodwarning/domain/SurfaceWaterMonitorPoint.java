package com.example.service.floodwarning.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity(name = "SurfaceWaterMonitorPoint")
@Table(name = "SurfaceWaterMonitorPoint")
public class SurfaceWaterMonitorPoint implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String name;
    @Column(name="station_id", unique=true)
    private String stationId;
    private int floodMinor;
    private int floodModerate;
    private int floodMajor;
    private BigDecimal lat;
    private BigDecimal lon;
}
