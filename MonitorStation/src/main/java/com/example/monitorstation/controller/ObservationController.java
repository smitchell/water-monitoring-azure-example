package com.example.monitorstation.controller;

import com.example.monitorstation.domain.Observation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/observation")
public class ObservationController {

    private Observation lastObservation;

    @Autowired
    public ObservationController(Observation lastObservation) {
        this.lastObservation = lastObservation;
    }

    @GetMapping
    public Observation getLastObservation() {
        return lastObservation;
    }
}
