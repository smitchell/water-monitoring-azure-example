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

    public int processFloodAdvisoryEvent(ApplicationEvent floodAdvisoryEvent) throws Exception {
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
        int count = 0;
        Iterable<NotificationPreference> matchedEmailRecipients = notificationPreferenceRepository.findAll();
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSentDate(new Date());
        email.setFrom("no-reply@sink.sendgrid.net");
        email.setReplyTo("no-reply@sink.sendgrid.net");
        email.setSubject(description);
        email.setText(String.format("<html><head><title>Flood Advisory</head><body><h1>%s</h1><body></html>", description));
        log.info("\n\tStarting queuing of matched recipients.");
        for (NotificationPreference preference : matchedEmailRecipients) {
            if (preference.isEmailEnabled()) {
                email.setTo(preference.getEmailAddress());
                queueService.queueEmail(email);
                count++;
            }
        }
        log.info(String.format("\n\tFinished queuing %s matched recipients.", count));
        return count;
    }
}
