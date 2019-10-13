package com.example.service.floodwarning.test.repository;

import com.example.service.floodwarning.domain.SurfaceWaterMonitorPoint;
import com.example.service.floodwarning.repository.SurfaceWaterMonitorPointRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SurfaceWaterMonitorPointRepositoryTest {

    @Autowired
    private SurfaceWaterMonitorPointRepository surfaceWaterMonitorPointRepository;

    @Test
    public void testCreateSurfaceWaterMonitorPoint() {
        final SurfaceWaterMonitorPoint surfaceWaterMonitorPoint = generateSurfaceWaterMonitorPoint();
        SurfaceWaterMonitorPoint savedEntity = surfaceWaterMonitorPointRepository.save(surfaceWaterMonitorPoint);
        assertThat(savedEntity, notNullValue());
        assertThat(savedEntity.getId(), notNullValue());
        assertThat(savedEntity.getName(), equalTo(surfaceWaterMonitorPoint.getName()));
        assertThat(savedEntity.getStationId(), equalTo(surfaceWaterMonitorPoint.getStationId()));
        assertThat(savedEntity.getFloodMinor(), equalTo(surfaceWaterMonitorPoint.getFloodMinor()));
        assertThat(savedEntity.getFloodModerate(), equalTo(surfaceWaterMonitorPoint.getFloodModerate()));
        assertThat(savedEntity.getFloodMajor(), equalTo(surfaceWaterMonitorPoint.getFloodMajor()));
    }

    @Test
    public void testGetSurfaceWaterMonitorPoint() {
        final SurfaceWaterMonitorPoint surfaceWaterMonitorPoint = generateSurfaceWaterMonitorPoint();
        SurfaceWaterMonitorPoint savedEntity = surfaceWaterMonitorPointRepository.save(surfaceWaterMonitorPoint);
        Optional<SurfaceWaterMonitorPoint> optional = surfaceWaterMonitorPointRepository.findById(savedEntity.getId());
        assertThat(optional, notNullValue());
        assertThat(optional.isPresent(), equalTo(true));

    }

    @Test
    public void testFindSurfaceWaterMonitorPointByStationId() {
        final SurfaceWaterMonitorPoint surfaceWaterMonitorPoint = generateSurfaceWaterMonitorPoint();
        SurfaceWaterMonitorPoint savedEntity = surfaceWaterMonitorPointRepository.save(surfaceWaterMonitorPoint);

        Optional<SurfaceWaterMonitorPoint> optional = surfaceWaterMonitorPointRepository.findByStationId(savedEntity.getStationId());
        assertThat(optional, notNullValue());
        assertThat(optional.isPresent(), equalTo(true));

    }

    private SurfaceWaterMonitorPoint generateSurfaceWaterMonitorPoint() {
        final SurfaceWaterMonitorPoint surfaceWaterMonitorPoint = new SurfaceWaterMonitorPoint();
        surfaceWaterMonitorPoint.setName(RandomStringUtils.randomAlphabetic(50));
        surfaceWaterMonitorPoint.setStationId(RandomStringUtils.randomAlphabetic(10));
        surfaceWaterMonitorPoint.setFloodMinor(20);
        surfaceWaterMonitorPoint.setFloodModerate(24);
        surfaceWaterMonitorPoint.setFloodMajor(30);
        surfaceWaterMonitorPoint.setLat(BigDecimal.valueOf(39.326944).setScale(6, RoundingMode.HALF_UP));
        surfaceWaterMonitorPoint.setLon(BigDecimal.valueOf(-94.909444).setScale(6, RoundingMode.HALF_UP));
        return surfaceWaterMonitorPoint;
    }
}
