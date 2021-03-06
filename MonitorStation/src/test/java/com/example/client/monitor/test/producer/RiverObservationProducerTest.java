package com.example.client.monitor.test.producer;

import com.example.client.monitor.domain.Observation;
import com.example.client.monitor.event.ApplicationEvent;
import com.example.client.monitor.producer.RiverObservationProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class RiverObservationProducerTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private RiverObservationProducer riverObservationProducer;

    @Before
    public void runBefore() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPublish() throws JsonProcessingException {
        Observation o = new Observation();
        o.setStationId(RandomStringUtils.randomAlphabetic(10));
        o.setImageUrl("http://host/images/image.png");
        o.setWaterFlow(100);
        o.setWaterLevel(BigDecimal.valueOf(25).setScale(2, RoundingMode.HALF_UP));
        o.setLat(BigDecimal.valueOf(34).setScale(6, RoundingMode.HALF_UP));
        o.setLon(BigDecimal.valueOf(-94).setScale(6, RoundingMode.HALF_UP));
        o.setTime(new Date());
        ApplicationEvent event = riverObservationProducer.publish(o);

        assertThat(event, notNullValue());
        assertThat(event.getEventId(), notNullValue());
        assertThat(event.getCorrelationId(), equalTo(o.getStationId()));
        assertThat(event.getCorrelationId(), equalTo(o.getStationId()));
        assertThat(event.getCreatedAt(), equalTo(o.getTime()));
        assertThat(event.getEventType(), equalTo(RiverObservationProducer.EVENT_TYPE));
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(o);
        assertThat(event.getData(), equalTo(json));
    }
}
