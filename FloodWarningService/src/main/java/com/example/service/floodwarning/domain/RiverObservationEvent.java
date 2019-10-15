package com.example.service.floodwarning.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class RiverObservationEvent implements Serializable {
    private String eventId;
    private Date createdAt;
    private String eventType;
    private String correlationId;
    private String data;
}
