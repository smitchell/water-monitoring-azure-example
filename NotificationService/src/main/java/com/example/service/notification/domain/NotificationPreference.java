package com.example.service.notification.domain;

import com.sun.istack.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@Entity
public class NotificationPreference implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true)
    private String emailAddress;

    @Column(unique = true)
    private String mobileNumber;

    @NotNull
    private boolean emailEnabled;

    @NotNull
    private boolean smsEnabled;

    @NotNull
    @Column(nullable = false)
    private String county;

    @Column(nullable = false)
    @NotNull
    private String postal;

    @NotNull
    @Column(nullable = false)
    private String state;
}
