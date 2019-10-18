package com.example.service.notification.test.repository;

import com.example.service.notification.domain.NotificationPreference;
import com.example.service.notification.repository.NotificationPreferenceRepository;
import com.example.service.notification.test.TestHelper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
@RunWith(SpringRunner.class)
public class NotificationRepositoryTest {
    @Autowired
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @Test
    public void testInsertNotificationPreference() {
        NotificationPreference notificationPreference = TestHelper.generateNotificationPreference();
        notificationPreference = notificationPreferenceRepository.save(notificationPreference);
        Optional<NotificationPreference> optional = notificationPreferenceRepository.findById(notificationPreference.getId());
        assertThat(optional.isPresent(), equalTo(true));
    }

    @Test
    public void testUpdateNotificationPreference() {
        NotificationPreference notificationPreference = TestHelper.generateNotificationPreference();
        notificationPreference = notificationPreferenceRepository.save(notificationPreference);
        Optional<NotificationPreference> optional = notificationPreferenceRepository.findById(notificationPreference.getId());
        assertThat(optional.isPresent(), equalTo(true));

        String newEmail = RandomStringUtils.randomAlphabetic(10).concat("@sink.sendgrid.net");
        notificationPreference.setEmailAddress(newEmail);
        notificationPreferenceRepository.save(notificationPreference);
        optional = notificationPreferenceRepository.findById(notificationPreference.getId());
        assertThat(optional.isPresent(), equalTo(true));

        assertThat(optional.get().getEmailAddress(), equalTo(newEmail));
    }

    @Test
    public void testFindByEmailAddress() {
        NotificationPreference notificationPreference = TestHelper.generateNotificationPreference();
        notificationPreference = notificationPreferenceRepository.save(notificationPreference);
        Optional<NotificationPreference> optional = notificationPreferenceRepository.findById(notificationPreference.getId());
        assertThat(optional.isPresent(), equalTo(true));

        notificationPreference = notificationPreferenceRepository.findByEmailAddress(notificationPreference.getEmailAddress());
        assertThat(notificationPreference, notNullValue());
    }

    @Test
    public void testFindByMobileNumber() {
        NotificationPreference notificationPreference = TestHelper.generateNotificationPreference();
        notificationPreference = notificationPreferenceRepository.save(notificationPreference);
        Optional<NotificationPreference> optional = notificationPreferenceRepository.findById(notificationPreference.getId());
        assertThat(optional.isPresent(), equalTo(true));

        notificationPreference = notificationPreferenceRepository.findByMobileNumber(notificationPreference.getMobileNumber());
        assertThat(notificationPreference, notNullValue());
    }
}
