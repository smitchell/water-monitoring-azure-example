package com.example.service.floodwarning.repository;

import com.example.service.floodwarning.domain.Observation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObservationRepository extends CrudRepository<Observation, String> {
}
