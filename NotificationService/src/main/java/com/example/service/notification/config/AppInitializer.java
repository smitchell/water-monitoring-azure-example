package com.example.service.notification.config;

import com.example.service.notification.domain.NotificationPreference;
import com.example.service.notification.repository.NotificationPreferenceRepository;
import org.ajbrown.namemachine.Name;
import org.ajbrown.namemachine.NameGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
public class AppInitializer {
    private final int seedEmailCount;
    private final NotificationPreferenceRepository notificationPreferenceRepository;

    @Autowired
    public AppInitializer(
            final NotificationPreferenceRepository notificationPreferenceRepository,
            @Value("${seed-email-count}") final int seedEmailCount) {
        this.notificationPreferenceRepository = notificationPreferenceRepository;
        this.seedEmailCount = seedEmailCount;
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
        List<NotificationPreference> preferences = new ArrayList<>();
        NameGenerator generator = new NameGenerator();
        List<Name> names = generator.generateNames(seedEmailCount);
       for(Name name : names) {
           // This simulation does not handle duplicate names.
           String emailAddress = name.getFirstName().concat(".").concat(name.getLastName()).concat("@sink.sendgrid.net");
           NotificationPreference recipient = notificationPreferenceRepository.findByEmailAddress(emailAddress);
           if (recipient == null) {
               preferences.add(generateNotificationPreference(name));
           }
       }
       return preferences;
    }

    private NotificationPreference generateNotificationPreference(Name name) {
        final String state = "KS";
        final String county = "Leavenworth";
        final String postal = "county";
        final NotificationPreference notificationPreference = new NotificationPreference();
        notificationPreference.setFirstName(name.getFirstName());
        notificationPreference.setLastName(name.getLastName());
        notificationPreference.setMobileNumber(RandomStringUtils.randomNumeric(10));
        notificationPreference.setSmsEnabled(true);
        notificationPreference.setEmailAddress(name.getFirstName().concat(".").concat(name.getLastName()).concat("@sink.sendgrid.net"));
        notificationPreference.setEmailEnabled(true);
        notificationPreference.setState(state);
        notificationPreference.setCounty(county);
        notificationPreference.setPostal(postal);
        return notificationPreference;
    }
}
