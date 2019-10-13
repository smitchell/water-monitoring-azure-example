package com.example.service.floodwarning.test.config;

import com.example.service.floodwarning.config.AppInitializer;
import com.example.service.floodwarning.domain.SurfaceWaterMonitorPoint;
import com.example.service.floodwarning.repository.SurfaceWaterMonitorPointRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static com.example.service.floodwarning.config.AppInitializer.STATION_ID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AppInitializerTest {

    @Autowired
    private SurfaceWaterMonitorPointRepository surfaceWaterMonitorPointRepository;

    @Autowired
    private AppInitializer appInitializer;

    @Test
    public void testInitialize() {
        Optional<SurfaceWaterMonitorPoint> optional = surfaceWaterMonitorPointRepository.findByStationId(STATION_ID);
        optional.ifPresent(surfaceWaterMonitorPoint -> surfaceWaterMonitorPointRepository.delete(surfaceWaterMonitorPoint));
        appInitializer.initialize();
        optional = surfaceWaterMonitorPointRepository.findByStationId(STATION_ID);
        assertThat(optional.isPresent(), equalTo(true));
    }
}
