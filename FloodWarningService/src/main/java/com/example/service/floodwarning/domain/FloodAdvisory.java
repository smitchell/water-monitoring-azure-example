package com.example.service.floodwarning.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class FloodAdvisory {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @ManyToOne
    @JoinColumn(name = "fk_surface_water_monitor_point")
    private SurfaceWaterMonitorPoint surfaceWaterMonitorPoint;

    FloodAdvisoryType floodAdvisoryType;

    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date advisoryStartTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date advisoryEndTime;
}
