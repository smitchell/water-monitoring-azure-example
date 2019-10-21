package com.example.service.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueClient;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.net.URISyntaxException;

@Slf4j
@Service
public class QueueService {

    private final CloudStorageAccount cloudStorageAccount;
    private CloudQueue emailQueue;

    @Autowired
    public QueueService(CloudStorageAccount csa) {
        this.cloudStorageAccount = csa;
    }

    @PostConstruct
    public void postConstruct() throws URISyntaxException, StorageException {
        final String queueName = "emailqueue";
        Assert.notNull(cloudStorageAccount, "cloudStorageAccount must not be null");
        CloudQueueClient queueClient = cloudStorageAccount.createCloudQueueClient();
        emailQueue = queueClient.getQueueReference(queueName);
        emailQueue.createIfNotExists();
    }

    public void queueEmail(SimpleMailMessage email) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(email);
        emailQueue.addMessage(new CloudQueueMessage(json));
        log.debug(String.format("Queuing email %s - %s", email.getTo(), email.getSubject()));
    }


}
