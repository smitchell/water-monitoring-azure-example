package com.example.service.floodwarning.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

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
}
