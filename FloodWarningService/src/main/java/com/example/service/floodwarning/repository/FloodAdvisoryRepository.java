package com.example.service.floodwarning.repository;

import com.example.service.floodwarning.domain.FloodAdvisory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FloodAdvisoryRepository extends CrudRepository<FloodAdvisory, String> {
}
