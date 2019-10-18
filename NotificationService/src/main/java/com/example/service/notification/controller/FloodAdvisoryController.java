package com.example.service.notification.controller;

import com.example.service.notification.domain.NotificationPreference;
import com.example.service.notification.event.ApplicationEvent;
import com.example.service.notification.repository.NotificationPreferenceRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import java.io.IOException;

@Slf4j
@Controller
public class FloodAdvisoryController {

    private NotificationPreferenceRepository notificationPreferenceRepository;

    @Autowired
    public FloodAdvisoryController(NotificationPreferenceRepository notificationPreferenceRepository) {
        this.notificationPreferenceRepository = notificationPreferenceRepository;
    }

    public long processFloodAdvisoryEvent(ApplicationEvent floodAdvisoryEvent) throws IOException {
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
                log.info(String.format("Emailing %s - %s", preference.getEmailAddress(), description));
                count++;
            }
        }
        return count;
    }
}
