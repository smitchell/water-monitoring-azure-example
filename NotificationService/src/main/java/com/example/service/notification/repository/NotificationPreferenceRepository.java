package com.example.service.notification.repository;

import com.example.service.notification.domain.NotificationPreference;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface NotificationPreferenceRepository extends CrudRepository<NotificationPreference, String> {

    NotificationPreference findByEmailAddress(@Param("emailAddress") String emailAddress);

    NotificationPreference findByMobileNumber(@Param("mobileNumber") String mobileNumber);
}
