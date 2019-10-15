package com.example.service.floodwarning.consumer;

import com.example.service.floodwarning.controller.RiverObservationController;
import com.example.service.floodwarning.domain.RiverObservationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class RiverObservationConsumer {

    private static final String TOPIC_NAME = "riverobservationstopic";
    private static final String SUBSCRIPTION_NAME = "FloodMonitoring";
    private final RiverObservationController riverObservationController;

    @Autowired
    public RiverObservationConsumer(RiverObservationController riverObservationController) {
        this.riverObservationController = riverObservationController;
    }

    @JmsListener(destination = TOPIC_NAME, containerFactory = "topicJmsListenerContainerFactory",
            subscription = SUBSCRIPTION_NAME)
    public void onMessage(Message<String> msg) {
        try {
            RiverObservationEvent event = parseRiverObservationMessage(msg);
            log.info("onMessage: " + event);
            riverObservationController.processProcessRiverObservation(event);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public RiverObservationEvent parseRiverObservationMessage(Message<String> msg) throws IOException {
        log.info("Message headers: " + msg.getHeaders());
        RiverObservationEvent event = null;
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(msg.getPayload(), RiverObservationEvent.class);
    }
}
