package com.example.service.floodwarning.consumer;

import com.example.service.floodwarning.domain.RiverObservationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class RiverObservationConsumer {

    private static final String TOPIC_NAME = "riverobservationstopic";

    private static final String SUBSCRIPTION_NAME = "FloodMonitoring";

    @JmsListener(destination = TOPIC_NAME, containerFactory = "topicJmsListenerContainerFactory",
            subscription = SUBSCRIPTION_NAME)
    public void onMessage(Message<String> msg) {
        log.info("onMessage: " + msg.getPayload());
    }

    public RiverObservationEvent parseRiverObservationMessage(Message<String> msg) {
        log.info("Message headers: " + msg.getHeaders());
        RiverObservationEvent event = null;
        if (msg != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                event = mapper.readValue(msg.getPayload(), RiverObservationEvent.class);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return event;
    }
}
