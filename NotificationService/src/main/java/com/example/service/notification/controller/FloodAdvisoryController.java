package com.example.service.notification.controller;

import com.example.service.notification.domain.NotificationPreference;
import com.example.service.notification.event.ApplicationEvent;
import com.example.service.notification.repository.NotificationPreferenceRepository;
import com.example.service.notification.service.QueueService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Date;

@Slf4j
@Controller
public class FloodAdvisoryController {
    private QueueService queueService;
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @Autowired
    public FloodAdvisoryController(QueueService queueService, NotificationPreferenceRepository notificationPreferenceRepository) {
        this.queueService = queueService;
        this.notificationPreferenceRepository = notificationPreferenceRepository;
    }

    public long processFloodAdvisoryEvent(ApplicationEvent floodAdvisoryEvent) throws Exception {
        String description = null;
        Double lat = null;
        Double lon = null;
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(floodAdvisoryEvent.getData());
        if (rootNode.hasNonNull("description")) {
            description = rootNode.path("description").asText();
        }
        JsonNode stationNode = rootNode.findPath("surfaceWaterMonitorPoint") ;
        if (stationNode != null) {
            if (stationNode.hasNonNull("lat")) {
                lat = stationNode.path("lat").asDouble();
            }
            if (stationNode.hasNonNull("lon")) {
                lon = stationNode.path("lon").asDouble();
            }
        }
        Assert.notNull(description, "Lat must not be null;");
        Assert.notNull(lat, "Lat must not be null;");
        Assert.notNull(lon, "Lat must not be null;");
        /* *******************************************************
         *** This is where spatial matching magic would occur. ***
         *** We'll return everybody for this example.          ***
         ******************************************************* */
        long count = 0;
        Iterable<NotificationPreference> iterable = notificationPreferenceRepository.findAll();
        for (NotificationPreference preference : iterable) {
            if (preference.isEmailEnabled()) {
                SimpleMailMessage email = new SimpleMailMessage();
                email.setSentDate(new Date());
                email.setFrom("admin@sink.sendgrid.net");
                email.setReplyTo("DO_NOT_REPLY@sink.sendgrid.net");
                email.setTo(preference.getEmailAddress());
                email.setSubject(description);
                email.setText(String.format("<html><head><title>Flood Advisory</head><body><h1>%s</h1><body></html>", description));
                queueService.queueEmail(email);
                count++;
            }
        }
        return count;
    }
}
