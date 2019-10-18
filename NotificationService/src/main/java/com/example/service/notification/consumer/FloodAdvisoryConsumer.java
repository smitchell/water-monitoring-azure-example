package com.example.service.notification.consumer;

import com.example.service.notification.controller.FloodAdvisoryController;
import com.example.service.notification.event.ApplicationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class FloodAdvisoryConsumer {

    private static final String TOPIC_NAME = "floodadvisorytopic";
    private static final String SUBSCRIPTION_NAME = "FloodAdvisoryConsumer";
    private final FloodAdvisoryController floodAdvisoryController;

    @Autowired
    public FloodAdvisoryConsumer(FloodAdvisoryController floodAdvisoryController) {
        this.floodAdvisoryController = floodAdvisoryController;
    }

    @JmsListener(destination = TOPIC_NAME, containerFactory = "topicJmsListenerContainerFactory",
            subscription = SUBSCRIPTION_NAME)
    public void onMessage(Message<String> msg) {
        try {
            ApplicationEvent event = parseFloodAdvisoryMessage(msg);
            log.debug("onMessage: " + event);
            floodAdvisoryController.processFloodAdvisoryEvent(event);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public ApplicationEvent parseFloodAdvisoryMessage(Message<String> msg) throws IOException {
        ApplicationEvent event = null;
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(msg.getPayload(), ApplicationEvent.class);
    }
}
