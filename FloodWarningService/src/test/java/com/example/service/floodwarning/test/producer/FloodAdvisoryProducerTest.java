package com.example.service.floodwarning.test.producer;

import com.example.service.floodwarning.domain.FloodAdvisory;
import com.example.service.floodwarning.event.ApplicationEvent;
import com.example.service.floodwarning.producer.FloodAdvisoryProducer;
import com.example.service.floodwarning.test.TestHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsTemplate;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class FloodAdvisoryProducerTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private FloodAdvisoryProducer floodAdvisoryProducer;

    @Before
    public void runBefore() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPublish() throws JsonProcessingException {
        FloodAdvisory floodAdvisory = new FloodAdvisory();
        floodAdvisory.setId(UUID.randomUUID().toString());
        floodAdvisory.setSurfaceWaterMonitorPoint(TestHelper.generateSurfaceWaterMonitorPoint());
        floodAdvisory.setStationId(floodAdvisory.getSurfaceWaterMonitorPoint().getStationId());
        floodAdvisory.setFloodAdvisoryType(FloodAdvisory.TYPE_MODERATE);
        floodAdvisory.setAdvisoryStartTime(new Date());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 2);
        floodAdvisory.setAdvisoryEndTime(cal.getTime());
        floodAdvisory.setDescription(RandomStringUtils.randomAlphabetic(50));
        ApplicationEvent event = floodAdvisoryProducer.publish(floodAdvisory);

        assertThat(event, notNullValue());
        assertThat(event.getEventId(), notNullValue());
        assertThat(event.getCorrelationId(), equalTo(floodAdvisory.getSurfaceWaterMonitorPoint().getStationId()));
        assertThat(event.getCreatedAt(), equalTo(floodAdvisory.getAdvisoryStartTime()));
        assertThat(event.getEventType(), equalTo(FloodAdvisoryProducer.EVENT_TYPE));
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(floodAdvisory);
        assertThat(event.getData(), equalTo(json));
    }
}
