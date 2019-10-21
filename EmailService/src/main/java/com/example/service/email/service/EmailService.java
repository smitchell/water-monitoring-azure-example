package com.example.service.email.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {


    public void send(SimpleMailMessage email) {
       log.debug(email.getTo()[0] + " " + email.getText());
    }

}
