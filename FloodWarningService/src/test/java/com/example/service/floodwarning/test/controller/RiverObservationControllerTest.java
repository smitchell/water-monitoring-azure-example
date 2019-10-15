package com.example.service.floodwarning.test.controller;

import com.example.service.floodwarning.controller.RiverObservationController;
import com.example.service.floodwarning.domain.*;
import com.example.service.floodwarning.repository.FloodAdvisoryRepository;
import com.example.service.floodwarning.repository.ObservationRepository;
import com.example.service.floodwarning.repository.SurfaceWaterMonitorPointRepository;
import com.example.service.floodwarning.test.TestHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class RiverObservationControllerTest {

    @Mock
    private FloodAdvisoryRepository floodAdvisoryRepository;

    @Mock
    private ObservationRepository observationRepository;

    @Mock
    private SurfaceWaterMonitorPointRepository surfaceWaterMonitorPointRepository;

    @InjectMocks
    private RiverObservationController riverObservationController;

    private RiverObservationEvent riverObservationEvent;

    private Observation observation;

    private SurfaceWaterMonitorPoint surfaceWaterMonitorPoint;

    @Before
    public void runBefore() throws JsonProcessingException {
        MockitoAnnotations.initMocks(this);
        surfaceWaterMonitorPoint = TestHelper.generateSurfaceWaterMonitorPoint();

        observation = new Observation();
        observation.setId(UUID.randomUUID().toString());
        observation.setTime(new Date());
        observation.setStationId("LEVK1");
        observation.setWaterFlow(100);
        observation.setWaterLevel(BigDecimal.valueOf(26.80).setScale(2, RoundingMode.HALF_UP));
        observation.setLat(BigDecimal.valueOf(39.326944).setScale(6, RoundingMode.HALF_UP));
        observation.setLon(BigDecimal.valueOf(-94.909444).setScale(6, RoundingMode.HALF_UP));
        observation.setImageUrl("http://nowhere");
        riverObservationEvent = new RiverObservationEvent();
        riverObservationEvent.setEventId("43211f7b-9b41-4df9-99e3-534ea5f80e69");
        riverObservationEvent.setCreatedAt(new Date());
        riverObservationEvent.setEventType("SURFACE_WATER_OBSERVATION");
        riverObservationEvent.setCorrelationId("LEVK1");
        ObjectMapper mapper = new ObjectMapper();
        riverObservationEvent.setData(mapper.writeValueAsString(observation));

        FloodAdvisory floodAdvisory = new FloodAdvisory();
        floodAdvisory.setId(UUID.randomUUID().toString());
        when(floodAdvisoryRepository.save(any(FloodAdvisory.class))).thenReturn(floodAdvisory);
        when(surfaceWaterMonitorPointRepository.findByStationId(observation.getStationId())).thenReturn(Optional.of(surfaceWaterMonitorPoint));
        when(observationRepository.save(any(Observation.class))).thenReturn(observation);
        when(observationRepository.findById(observation.getId())).thenReturn(Optional.of(observation));
    }

    @Test
    public void testProcessRiverObservation() throws Exception {
        riverObservationController.processProcessRiverObservation(riverObservationEvent);
        verify(observationRepository, times(1)).save(any(Observation.class));
        verify(floodAdvisoryRepository, times(1)).save(any(FloodAdvisory.class));
    }

    @Test
    public void testComputeFloodAdvisory_None() {
        observation.setWaterLevel(surfaceWaterMonitorPoint.getFloodMinor().subtract(BigDecimal.valueOf(1)));

        Optional<FloodAdvisory> optional = riverObservationController.computeFloodAdvisory(observation);
        assertThat(optional.isPresent(), equalTo(false));
    }

    @Test
    public void testComputeFloodAdvisory_Minor() {
        observation.setWaterLevel(surfaceWaterMonitorPoint.getFloodMinor().add(BigDecimal.valueOf(1)));
        Optional<FloodAdvisory> optional = riverObservationController.computeFloodAdvisory(observation);
        assertThat(optional.isPresent(), equalTo(true));
        FloodAdvisory floodAdvisory = optional.get();
        assertThat(floodAdvisory.getFloodAdvisoryType(), equalTo(FloodAdvisoryType.MINOR));
        verify(floodAdvisoryRepository, times(1)).save(floodAdvisory);
    }

    @Test
    public void testComputeFloodAdvisory_Moderate() {
        observation.setWaterLevel(surfaceWaterMonitorPoint.getFloodModerate().add(BigDecimal.valueOf(1)));
        Optional<FloodAdvisory> optional = riverObservationController.computeFloodAdvisory(observation);
        assertThat(optional.isPresent(), equalTo(true));
        FloodAdvisory floodAdvisory = optional.get();
        assertThat(floodAdvisory.getFloodAdvisoryType(), equalTo(FloodAdvisoryType.MODERATE));
        verify(floodAdvisoryRepository, times(1)).save(floodAdvisory);
    }

    @Test
    public void testComputeFloodAdvisory_Major() {
        observation.setWaterLevel(surfaceWaterMonitorPoint.getFloodMajor().add(BigDecimal.valueOf(1)));
        Optional<FloodAdvisory> optional = riverObservationController.computeFloodAdvisory(observation);
        assertThat(optional.isPresent(), equalTo(true));
        FloodAdvisory floodAdvisory = optional.get();
        assertThat(floodAdvisory.getFloodAdvisoryType(), equalTo(FloodAdvisoryType.MAJOR));
    }

    @Test
    public void testGenerateFloodAdvisory() {
        observation.setWaterLevel(surfaceWaterMonitorPoint.getFloodMinor().add(BigDecimal.valueOf(1)));
        FloodAdvisory floodAdvisory = riverObservationController.generateFloodAdvisory(FloodAdvisoryType.MINOR, observation, surfaceWaterMonitorPoint);
        assertThat(floodAdvisory.getFloodAdvisoryType(), equalTo(FloodAdvisoryType.MINOR));
        assertThat(floodAdvisory.getDescription(), containsString(FloodAdvisoryType.MINOR.name()));
        assertThat(floodAdvisory.getDescription(), containsString(surfaceWaterMonitorPoint.getName()));
    }

}
