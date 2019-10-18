package com.example.service.notification.config;

import com.example.service.notification.domain.NotificationPreference;
import com.example.service.notification.repository.NotificationPreferenceRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


@Component
public class AppInitializer {
    public static long SEED_COUNT = 500;
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @Autowired
    public AppInitializer(NotificationPreferenceRepository notificationPreferenceRepository) {
        this.notificationPreferenceRepository = notificationPreferenceRepository;
    }

    @PostConstruct
    public long seedDatabase() {
        long count = notificationPreferenceRepository.count();
        if (count == 0) {
            notificationPreferenceRepository.saveAll(generateNotificationPreferences());
            count = notificationPreferenceRepository.count();
        }
        return count;
    }

    private List<NotificationPreference> generateNotificationPreferences() {
       int count = 0;
        List<NotificationPreference> preferences = new ArrayList<>(500);
       while(count++ < SEED_COUNT) {
           preferences.add(generateNotificationPreference());
       }
       return preferences;
    }

    private NotificationPreference generateNotificationPreference() {
        final String state = "KS";
        final String county = "Leavenworth";
        final String postal = "county";
        final NotificationPreference notificationPreference = new NotificationPreference();
        notificationPreference.setMobileNumber(RandomStringUtils.randomNumeric(10));
        notificationPreference.setSmsEnabled(true);
        notificationPreference.setEmailAddress(RandomStringUtils.randomAlphabetic(10).concat("@sink.sendgrid.net"));
        notificationPreference.setEmailEnabled(true);
        notificationPreference.setState(state);
        notificationPreference.setCounty(county);
        notificationPreference.setPostal(postal);
        return notificationPreference;
    }
}
