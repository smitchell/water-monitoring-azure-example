package com.example.service.floodwarning.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

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
    private Date time;
    private String stationId;
    private BigDecimal waterLevel;
    private Integer waterFlow;
    private BigDecimal lat;
    private BigDecimal lon;
    private String photoUrl;
}
