package com.example.service.floodwarning.producer;

import com.example.service.floodwarning.domain.FloodAdvisory;
import com.example.service.floodwarning.event.ApplicationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class FloodAdvisoryProducer {
    public static final String DESTINATION_NAME = "floodadvisorytopic";
    public static final String EVENT_TYPE = "FLOOD_ADVISORY";

    private JmsTemplate jmsTemplate;

    @Autowired
    public FloodAdvisoryProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public ApplicationEvent publish(FloodAdvisory floodAdvisory) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ApplicationEvent event = new ApplicationEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setCorrelationId(floodAdvisory.getSurfaceWaterMonitorPoint().getStationId());
        event.setCreatedAt(floodAdvisory.getAdvisoryStartTime());
        event.setEventType(EVENT_TYPE);
        event.setData(mapper.writeValueAsString(floodAdvisory));
        log.info(event.getEventId() + ": " + event.getData());
        String msg = mapper.writeValueAsString(event);
        jmsTemplate.send(DESTINATION_NAME, session -> session.createTextMessage(msg));
        log.info("Published: " + msg);
        return event;
    }

}
