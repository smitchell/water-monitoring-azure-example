package com.example.service.floodwarning.test.repository;

import com.example.service.floodwarning.domain.FloodAdvisory;
import com.example.service.floodwarning.domain.SurfaceWaterMonitorPoint;
import com.example.service.floodwarning.repository.FloodAdvisoryRepository;
import com.example.service.floodwarning.repository.SurfaceWaterMonitorPointRepository;
import com.example.service.floodwarning.test.TestHelper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
@RunWith(SpringRunner.class)
public class FloodAdvisoryRepositoryTest {

    @Autowired
    private SurfaceWaterMonitorPointRepository surfaceWaterMonitorPointRepository;

    @Autowired
    private FloodAdvisoryRepository floodAdvisoryRepository;

    @Test
    public void testFindBySurfaceWaterMonitorPointStationId() {
        SurfaceWaterMonitorPoint surfaceWaterMonitorPoint = surfaceWaterMonitorPointRepository.save(TestHelper.generateSurfaceWaterMonitorPoint());

        FloodAdvisory floodAdvisory = new FloodAdvisory();
        floodAdvisory.setStationId(surfaceWaterMonitorPoint.getStationId());
        floodAdvisory.setFloodAdvisoryStatus(FloodAdvisory.STATUS_ACTIVE);
        floodAdvisory.setFloodAdvisoryType(FloodAdvisory.TYPE_MODERATE);
        floodAdvisory.setAdvisoryStartTime(new Date());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 2);
        floodAdvisory.setAdvisoryEndTime(cal.getTime());
        floodAdvisory.setSurfaceWaterMonitorPoint(surfaceWaterMonitorPoint);
        floodAdvisory.setDescription(RandomStringUtils.randomAlphabetic(50));

        floodAdvisory = floodAdvisoryRepository.save(floodAdvisory);

        assertThat(floodAdvisory, notNullValue());
        assertThat(floodAdvisory.getId(), notNullValue());
        assertThat(floodAdvisory.getSurfaceWaterMonitorPoint(), notNullValue());
        assertThat(floodAdvisory.getSurfaceWaterMonitorPoint(), equalTo(surfaceWaterMonitorPoint));
        assertThat(floodAdvisory.getSurfaceWaterMonitorPoint().getStationId(), notNullValue());

        List<FloodAdvisory> activeFloodAdvisories =
                floodAdvisoryRepository.findBySurfaceWaterMonitorPointStationIdAndFloodAdvisoryStatusOrderByAdvisoryStartTimeDesc(
                        floodAdvisory.getSurfaceWaterMonitorPoint().getStationId(), FloodAdvisory.STATUS_ACTIVE);
        assertThat(activeFloodAdvisories.isEmpty(), is(false));

        floodAdvisory.setFloodAdvisoryStatus(FloodAdvisory.STATUS_EXPIRED);
        floodAdvisoryRepository.save(floodAdvisory);
        activeFloodAdvisories =
                floodAdvisoryRepository.findBySurfaceWaterMonitorPointStationIdAndFloodAdvisoryStatusOrderByAdvisoryStartTimeDesc(
                        floodAdvisory.getSurfaceWaterMonitorPoint().getStationId(), FloodAdvisory.STATUS_ACTIVE);
        assertThat(activeFloodAdvisories.isEmpty(), is(true));
    }

}
