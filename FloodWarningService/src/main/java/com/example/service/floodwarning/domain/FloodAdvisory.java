package com.example.service.floodwarning.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class FloodAdvisory {
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_EXPIRED = "EXPIRED";
    public static final String STATUS_CANCELED = "CANCELED";
    public static final String TYPE_MINOR = "MINOR";
    public static final String TYPE_MODERATE = "MODERATE";
    public static final String TYPE_MAJOR = "MAJOR";

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(nullable = false)
    private String stationId;

    @ManyToOne
    @JoinColumn(name = "fk_surface_water_monitor_point")
    private SurfaceWaterMonitorPoint surfaceWaterMonitorPoint;

    @Column(nullable = false)
    private String floodAdvisoryStatus;

    @Column(nullable = false)
    private String floodAdvisoryType;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date advisoryStartTime;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date advisoryEndTime;
}
