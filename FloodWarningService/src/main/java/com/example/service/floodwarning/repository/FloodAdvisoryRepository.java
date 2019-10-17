package com.example.service.floodwarning.repository;

import com.example.service.floodwarning.domain.FloodAdvisory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface FloodAdvisoryRepository extends CrudRepository<FloodAdvisory, String> {

    List<FloodAdvisory> findBySurfaceWaterMonitorPointStationIdAndFloodAdvisoryStatusOrderByAdvisoryStartTimeDesc(String stationId, String status);

}
