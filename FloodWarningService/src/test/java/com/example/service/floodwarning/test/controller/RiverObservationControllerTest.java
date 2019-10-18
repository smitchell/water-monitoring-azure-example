package com.example.service.floodwarning.test.controller;

import com.example.service.floodwarning.controller.RiverObservationController;
import com.example.service.floodwarning.domain.FloodAdvisory;
import com.example.service.floodwarning.domain.Observation;
import com.example.service.floodwarning.domain.SurfaceWaterMonitorPoint;
import com.example.service.floodwarning.event.ApplicationEvent;
import com.example.service.floodwarning.repository.FloodAdvisoryRepository;
import com.example.service.floodwarning.repository.ObservationRepository;
import com.example.service.floodwarning.repository.SurfaceWaterMonitorPointRepository;
import com.example.service.floodwarning.test.TestHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RiverObservationControllerTest {

    @Mock
    private FloodAdvisoryRepository floodAdvisoryRepository;

    @Mock
    private ObservationRepository observationRepository;

    @Mock
    private SurfaceWaterMonitorPointRepository surfaceWaterMonitorPointRepository;

    @InjectMocks
    private RiverObservationController riverObservationController;

    private ApplicationEvent riverObservationEvent;

    private Observation observation;

    private SurfaceWaterMonitorPoint surfaceWaterMonitorPoint;

    private MockRestServiceServer mockServer;

    @Before
    public void runBefore() throws JsonProcessingException {
        MockitoAnnotations.initMocks(this);
        surfaceWaterMonitorPoint = TestHelper.generateSurfaceWaterMonitorPoint();
        when(surfaceWaterMonitorPointRepository.findByStationId(anyString())).thenReturn(Optional.of(surfaceWaterMonitorPoint));

        observation = new Observation();
        observation.setId(UUID.randomUUID().toString());
        observation.setTime(new Date());
        observation.setStationId(surfaceWaterMonitorPoint.getStationId());
        observation.setWaterFlow(100);
        observation.setWaterLevel(BigDecimal.valueOf(26.80).setScale(2, RoundingMode.HALF_UP));
        observation.setLat(BigDecimal.valueOf(39.326944).setScale(6, RoundingMode.HALF_UP));
        observation.setLon(BigDecimal.valueOf(-94.909444).setScale(6, RoundingMode.HALF_UP));
        observation.setImageUrl("http://nowhere");
        when(observationRepository.save(any(Observation.class))).thenReturn(observation);
        when(observationRepository.findById(observation.getId())).thenReturn(Optional.of(observation));

        riverObservationEvent = new ApplicationEvent();
        riverObservationEvent.setEventId("43211f7b-9b41-4df9-99e3-534ea5f80e69");
        riverObservationEvent.setCreatedAt(new Date());
        riverObservationEvent.setEventType("SURFACE_WATER_OBSERVATION");
        riverObservationEvent.setCorrelationId(surfaceWaterMonitorPoint.getStationId());
        ObjectMapper mapper = new ObjectMapper();
        riverObservationEvent.setData(mapper.writeValueAsString(observation));

        FloodAdvisory floodAdvisory = new FloodAdvisory();
        floodAdvisory.setId(UUID.randomUUID().toString());
        floodAdvisory.setStationId(surfaceWaterMonitorPoint.getStationId());
        floodAdvisory.setAdvisoryStartTime(new Date());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 2);
        floodAdvisory.setAdvisoryEndTime(cal.getTime());
        floodAdvisory.setFloodAdvisoryStatus(FloodAdvisory.STATUS_ACTIVE);
        floodAdvisory.setFloodAdvisoryType(FloodAdvisory.TYPE_MAJOR);
        floodAdvisory.setDescription(RandomStringUtils.randomAlphabetic(50));
        floodAdvisory.setSurfaceWaterMonitorPoint(surfaceWaterMonitorPoint);
        when(floodAdvisoryRepository.save(any(FloodAdvisory.class))).thenReturn(floodAdvisory);

    }

    @Ignore
    public void testProcessRiverObservation() throws Exception {
        riverObservationController.processProcessRiverObservation(riverObservationEvent);
        verify(observationRepository, times(1)).save(any(Observation.class));
        verify(floodAdvisoryRepository, times(1)).save(any(FloodAdvisory.class));
    }

    @Ignore
    public void testComputeFloodAdvisory_None() throws Exception {
        observation.setWaterLevel(surfaceWaterMonitorPoint.getFloodMinor().subtract(BigDecimal.valueOf(1)));

        Optional<FloodAdvisory> optional = riverObservationController.computeFloodAdvisory(observation);
        assertThat(optional.isPresent(), equalTo(false));
    }

    @Ignore
    public void testComputeFloodAdvisory_Minor() throws Exception {
        observation.setWaterLevel(surfaceWaterMonitorPoint.getFloodMinor().add(BigDecimal.valueOf(1)));
        Optional<FloodAdvisory> optional = riverObservationController.computeFloodAdvisory(observation);
        assertThat(optional.isPresent(), equalTo(true));
        FloodAdvisory floodAdvisory = optional.get();
        assertThat(floodAdvisory.getFloodAdvisoryType(), equalTo(FloodAdvisory.TYPE_MINOR));
        verify(floodAdvisoryRepository, times(1)).save(floodAdvisory);
    }

    @Ignore
    public void testComputeFloodAdvisory_Moderate() throws Exception {
        observation.setWaterLevel(surfaceWaterMonitorPoint.getFloodModerate().add(BigDecimal.valueOf(1)));
        Optional<FloodAdvisory> optional = riverObservationController.computeFloodAdvisory(observation);
        assertThat(optional.isPresent(), equalTo(true));
        FloodAdvisory floodAdvisory = optional.get();
        assertThat(floodAdvisory.getFloodAdvisoryType(), equalTo(FloodAdvisory.TYPE_MODERATE));
        verify(floodAdvisoryRepository, times(1)).save(floodAdvisory);
    }

    @Ignore
    public void testComputeFloodAdvisory_Major() throws Exception {
        observation.setWaterLevel(surfaceWaterMonitorPoint.getFloodMajor().add(BigDecimal.valueOf(1)));
        Optional<FloodAdvisory> optional = riverObservationController.computeFloodAdvisory(observation);
        assertThat(optional.isPresent(), equalTo(true));
        FloodAdvisory floodAdvisory = optional.get();
        assertThat(floodAdvisory.getFloodAdvisoryType(), equalTo(FloodAdvisory.TYPE_MAJOR));
    }

    @Test
    public void testGenerateFloodAdvisory() {
        observation.setWaterLevel(surfaceWaterMonitorPoint.getFloodMinor().add(BigDecimal.valueOf(1)));
        FloodAdvisory floodAdvisory = riverObservationController.generateFloodAdvisory(FloodAdvisory.TYPE_MINOR, observation, surfaceWaterMonitorPoint);
        assertThat(floodAdvisory.getFloodAdvisoryType(), equalTo(FloodAdvisory.TYPE_MINOR));
        assertThat(floodAdvisory.getDescription(), containsString(FloodAdvisory.TYPE_MINOR));
        assertThat(floodAdvisory.getDescription(), containsString(surfaceWaterMonitorPoint.getName()));
    }

}
