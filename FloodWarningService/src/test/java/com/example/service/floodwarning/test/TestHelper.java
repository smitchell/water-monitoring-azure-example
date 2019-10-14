package com.example.service.floodwarning.test;

import com.example.service.floodwarning.domain.SurfaceWaterMonitorPoint;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TestHelper {

    public static SurfaceWaterMonitorPoint generateSurfaceWaterMonitorPoint() {
        final SurfaceWaterMonitorPoint surfaceWaterMonitorPoint = new SurfaceWaterMonitorPoint();
        surfaceWaterMonitorPoint.setName(RandomStringUtils.randomAlphabetic(50));
        surfaceWaterMonitorPoint.setStationId(RandomStringUtils.randomAlphabetic(10));
        surfaceWaterMonitorPoint.setFloodMinor(BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_UP));
        surfaceWaterMonitorPoint.setFloodModerate(BigDecimal.valueOf(24).setScale(2, RoundingMode.HALF_UP));
        surfaceWaterMonitorPoint.setFloodMajor(BigDecimal.valueOf(30).setScale(2, RoundingMode.HALF_UP));
        surfaceWaterMonitorPoint.setLat(BigDecimal.valueOf(39.326944).setScale(6, RoundingMode.HALF_UP));
        surfaceWaterMonitorPoint.setLon(BigDecimal.valueOf(-94.909444).setScale(6, RoundingMode.HALF_UP));
        return surfaceWaterMonitorPoint;
    }
}
