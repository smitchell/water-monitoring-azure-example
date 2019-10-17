package com.example.service.floodwarning.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
public class Observation implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    @Column(nullable = false)
    private Date time;
    @Column(nullable = false)
    private String stationId;
    @Column(nullable = false)
    private BigDecimal waterLevel;
    @Column(nullable = false)
    private Integer waterFlow;
    private BigDecimal lat;
    private BigDecimal lon;
    private String imageUrl;
}
