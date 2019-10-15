package com.example.service.floodwarning.test;

import com.example.service.floodwarning.consumer.RiverObservationConsumer;
import com.example.service.floodwarning.controller.RiverObservationController;
import com.example.service.floodwarning.domain.RiverObservationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

public class RiverObservationConsumerTest {

    @Mock
    private RiverObservationController riverObservationController;

    @InjectMocks
    private RiverObservationConsumer riverObservationConsumer;

    @Before
    public void runBefore() throws JsonProcessingException {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOnMessage() {
        String json =  "{\"eventId\":\"43211f7b-9b41-4df9-99e3-534ea5f80e69\",\"createdAt\":1571008564171,\"eventType\":\"SURFACE_WATER_OBSERVATION\",\"correlationId\":\"LEVK1;\",\"data\":\"{\"time\":1571008564171,\"stationId\":\"LEVK1;\",\"waterLevel\":26.80,\"waterFlow\":100,\"lat\":39.326944,\"lon\":-94.909444}\"}";

        riverObservationConsumer.onMessage(new Message<String>() {
            @Override
            public String getPayload() {
                return json;
            }

            @Override
            public MessageHeaders getHeaders() {
                return null;
            }
        });
    }

    @Test
    public void testParseRiverObservationMessage() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        RiverObservationEvent riverObservationEvent = new RiverObservationEvent();
        riverObservationEvent.setEventId("43211f7b-9b41-4df9-99e3-534ea5f80e69");
        riverObservationEvent.setCreatedAt(new Date());
        riverObservationEvent.setEventType("SURFACE_WATER_OBSERVATION");
        riverObservationEvent.setCorrelationId("LEVK1");
        riverObservationEvent.setData("\"{\"time\":1571008564171,\"stationId\":\"LEVK1;\",\"waterLevel\":26.80,\"waterFlow\":100,\"lat\":39.326944,\"lon\":-94.909444}\"}");
        String json = objectMapper.writeValueAsString(riverObservationEvent);

        RiverObservationEvent event = riverObservationConsumer.parseRiverObservationMessage(new Message<String>() {
            @Override
            public String getPayload() {
                return json;
            }

            @Override
            public MessageHeaders getHeaders() {
                return null;
            }
        });

        assertThat(event, notNullValue());
        JsonNode rootNode = objectMapper.readTree(json);
        assertThat(event.getEventId(), equalTo(riverObservationEvent.getEventId()));
        assertThat(event.getCorrelationId(), equalTo(riverObservationEvent.getCorrelationId()));
        assertThat(event.getCreatedAt(), equalTo(riverObservationEvent.getCreatedAt()));
        assertThat(event.getData(), equalTo(riverObservationEvent.getData()));
        assertThat(event.getEventType(), equalTo(riverObservationEvent.getEventType()));
    }

}
