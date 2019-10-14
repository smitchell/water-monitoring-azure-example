package com.example.service.floodwarning.config;

import com.example.service.floodwarning.domain.SurfaceWaterMonitorPoint;
import com.example.service.floodwarning.repository.SurfaceWaterMonitorPointRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Slf4j
@Component
public class AppInitializer {
    public static final String STATION_ID = "LEVK1";

    private SurfaceWaterMonitorPointRepository surfaceWaterMonitorPointRepository;

    @Autowired
    public AppInitializer(SurfaceWaterMonitorPointRepository surfaceWaterMonitorPointRepository) {
        this.surfaceWaterMonitorPointRepository = surfaceWaterMonitorPointRepository;
    }

    @PostConstruct
    public void initialize() {
        Optional<SurfaceWaterMonitorPoint> optional = surfaceWaterMonitorPointRepository.findByStationId(STATION_ID);
        if (!optional.isPresent()) {
            final SurfaceWaterMonitorPoint surfaceWaterMonitorPoint = new SurfaceWaterMonitorPoint();
            surfaceWaterMonitorPoint.setName(STATION_ID.concat(" Missouri River at Leavenworth (Kansas)"));
            surfaceWaterMonitorPoint.setStationId(STATION_ID);
            surfaceWaterMonitorPoint.setFloodMinor(BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_UP));
            surfaceWaterMonitorPoint.setFloodModerate(BigDecimal.valueOf(24).setScale(2, RoundingMode.HALF_UP));
            surfaceWaterMonitorPoint.setFloodMajor(BigDecimal.valueOf(30).setScale(2, RoundingMode.HALF_UP));
            surfaceWaterMonitorPoint.setLat(BigDecimal.valueOf(39.326944).setScale(6, RoundingMode.HALF_UP));
            surfaceWaterMonitorPoint.setLon(BigDecimal.valueOf(-94.909444).setScale(6, RoundingMode.HALF_UP));
            surfaceWaterMonitorPointRepository.save(surfaceWaterMonitorPoint);
            log.info("Seeded database: " + surfaceWaterMonitorPoint);
        }

    }
}
