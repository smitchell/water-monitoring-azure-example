package com.example.client.monitor.producer;

import com.example.client.monitor.domain.Observation;
import com.example.client.monitor.event.RiverObservationEvent;
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
    public static final String DESTINATION_NAME = "riverobservationstopic";
    public static final String EVENT_TYPE = "SURFACE_WATER_OBSERVATION";

    private JmsTemplate jmsTemplate;
    private String callbackUrl;

    @Autowired
    public RiverObservationProducer(JmsTemplate jmsTemplate, String callbackUrl) {
        this.jmsTemplate = jmsTemplate;
        this.callbackUrl = callbackUrl;
    }


    public RiverObservationEvent publish(Observation observation) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        RiverObservationEvent event = new RiverObservationEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setCorrelationId(observation.getStationId());
        event.setCreatedAt(observation.getTime());
        event.setEventType(EVENT_TYPE);
        event.setData(mapper.writeValueAsString(observation));
        event.setCallbackUrl(callbackUrl);
        log.info(event.getEventId() + ": " + event.getData());
        String msg = mapper.writeValueAsString(event);
        jmsTemplate.send(DESTINATION_NAME, session -> session.createTextMessage(msg));
        log.info("Published: " + msg);
        return event;
    }

}
