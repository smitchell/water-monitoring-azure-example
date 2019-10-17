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
    @Column(nullable = false)
    private String name;
    @Column(name="station_id", unique=true, nullable = false)
    private String stationId;
    @Column(nullable = false)
    private BigDecimal floodMinor;
    @Column(nullable = false)
    private BigDecimal floodModerate;
    @Column(nullable = false)
    private BigDecimal floodMajor;
    @Column(nullable = false)
    private BigDecimal lat;
    @Column(nullable = false)
    private BigDecimal lon;
}
