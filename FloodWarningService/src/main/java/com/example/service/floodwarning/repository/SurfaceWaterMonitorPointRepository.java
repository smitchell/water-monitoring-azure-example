package com.example.service.floodwarning.repository;

import com.example.service.floodwarning.domain.SurfaceWaterMonitorPoint;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SurfaceWaterMonitorPointRepository extends CrudRepository<SurfaceWaterMonitorPoint, String> {

    Optional<SurfaceWaterMonitorPoint> findByStationId(@Param("stationId") String stationId);
}
