package com.example.service.notification.test;

import com.example.service.notification.domain.NotificationPreference;
import com.example.service.notification.event.ApplicationEvent;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;
import java.util.UUID;

public class TestHelper {

    public static NotificationPreference generateNotificationPreference() {
        NotificationPreference notificationPreference = new NotificationPreference();
        notificationPreference.setMobileNumber(RandomStringUtils.randomNumeric(10));
        notificationPreference.setSmsEnabled(true);
        notificationPreference.setEmailAddress(RandomStringUtils.randomAlphabetic(10).concat("@sink.sendgrid.net"));
        notificationPreference.setEmailEnabled(true);
        notificationPreference.setState("KS");
        notificationPreference.setCounty("Leavenworth");
        notificationPreference.setPostal("66027");
        return notificationPreference;
    }

    public static ApplicationEvent generateFloodAdvisoryController() {
        ApplicationEvent event = new ApplicationEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setCorrelationId("LEVK1");
        event.setCreatedAt(new Date());
        event.setData("{\"id\": \"2896b641-8811-4ec2-9043-b2d9dfe4fae3\", \"stationId\": \"LEVK1\", \"surfaceWaterMonitorPoint\": {\"id\": \"1d6a208a-b26e-48a3-b0bc-28877066c7e0\", \"name\": \"LEVK1 Missouri River at Leavenworth (Kansas)\", \"stationId\": \"LEVK1\", \"floodMinor\": 20.00, \"floodModerate\": 24.00, \"floodMajor\": 30.00, \"lat\": 39.33, \"lon\": -94.91},\"floodAdvisoryStatus\": \"ACTIVE\", \"floodAdvisoryType\": \"MINOR\", \"description\": \"MINOR (21.00 ft) - Observation - LEVK1 Missouri River at Leavenworth (Kansas)\", \"advisoryStartTime\": 1571349314876, \"advisoryEndTime\": 1571349314876}");
        event.setEventType("FLOOD_ADVISORY");
        return event;
    }
}
