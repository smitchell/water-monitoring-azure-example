package com.example.service.floodwarning.test.repository;

import com.example.service.floodwarning.domain.SurfaceWaterMonitorPoint;
import com.example.service.floodwarning.repository.SurfaceWaterMonitorPointRepository;
import com.example.service.floodwarning.test.TestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
@RunWith(SpringRunner.class)
public class SurfaceWaterMonitorPointRepositoryTest {

    @Autowired
    private SurfaceWaterMonitorPointRepository surfaceWaterMonitorPointRepository;

    @Test
    public void testCreateSurfaceWaterMonitorPoint() {
        final SurfaceWaterMonitorPoint surfaceWaterMonitorPoint = TestHelper.generateSurfaceWaterMonitorPoint();
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
        final SurfaceWaterMonitorPoint surfaceWaterMonitorPoint = TestHelper.generateSurfaceWaterMonitorPoint();
        SurfaceWaterMonitorPoint savedEntity = surfaceWaterMonitorPointRepository.save(surfaceWaterMonitorPoint);
        Optional<SurfaceWaterMonitorPoint> optional = surfaceWaterMonitorPointRepository.findById(savedEntity.getId());
        assertThat(optional, notNullValue());
        assertThat(optional.isPresent(), equalTo(true));

    }

    @Test
    public void testFindSurfaceWaterMonitorPointByStationId() {
        final SurfaceWaterMonitorPoint surfaceWaterMonitorPoint = TestHelper.generateSurfaceWaterMonitorPoint();
        SurfaceWaterMonitorPoint savedEntity = surfaceWaterMonitorPointRepository.save(surfaceWaterMonitorPoint);

        Optional<SurfaceWaterMonitorPoint> optional = surfaceWaterMonitorPointRepository.findByStationId(savedEntity.getStationId());
        assertThat(optional, notNullValue());
        assertThat(optional.isPresent(), equalTo(true));

    }


}
