package com.example.monitorstation.producer;

import com.example.monitorstation.domain.Observation;
import com.example.monitorstation.event.RiverObservationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class RiverObservationProducer {
    private static final String DESTINATION_NAME = "riverobservationstopic";
    private static final String EVENT_TYPE = "RIVER_STATION_OBSERVATION";

    @Autowired
    private JmsTemplate jmsTemplate;

    public String publish(Observation observation) throws JsonProcessingException {
        log.info("Sending message");
        RiverObservationEvent event = new RiverObservationEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setCorrelationId(observation.getStationId());
        event.setCreatedAt(new Date());
        event.setEventType(EVENT_TYPE);
        event.setData(new ObjectMapper().writeValueAsString(observation));
        log.info(event.getEventId() + ": " + event.getData());
        jmsTemplate.convertAndSend(DESTINATION_NAME, event);
        return event.getEventId();
    }

}
