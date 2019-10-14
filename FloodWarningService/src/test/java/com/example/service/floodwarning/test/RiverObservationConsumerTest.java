package com.example.service.floodwarning.test;

import com.example.service.floodwarning.consumer.RiverObservationConsumer;
import com.example.service.floodwarning.domain.RiverObservationEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.io.IOException;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

public class RiverObservationConsumerTest {

    private RiverObservationConsumer riverObservationConsumer = new RiverObservationConsumer();

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
