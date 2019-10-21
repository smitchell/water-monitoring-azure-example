package com.example.service.email.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueClient;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
public class QueueService {

    private final EmailService emailService;
    private final CloudStorageAccount cloudStorageAccount;
    private CloudQueue emailQueue;

    @Autowired
    public QueueService(final CloudStorageAccount csa,
                        final EmailService emailService) {
        this.cloudStorageAccount = csa;
        this.emailService = emailService;
    }

    @PostConstruct
    public void postConstruct() throws Exception {
        final String queueName = "emailqueue";
        Assert.notNull(cloudStorageAccount, "cloudStorageAccount must not be null");
        CloudQueueClient queueClient = cloudStorageAccount.createCloudQueueClient();
        emailQueue = queueClient.getQueueReference(queueName);
        emailQueue.createIfNotExists();
    }

    // Initial delay prevents execution during unit tests.
    @Scheduled(fixedRate = 500, initialDelay = 10000)
    public void pollForMessages() throws Exception {
        log.debug("pollForMessages");
        SimpleMailMessage email;
        // Retrieve the first visible message in the queue.
        Iterable<CloudQueueMessage> iterable = emailQueue.retrieveMessages(32);
        StringBuilder sb = new StringBuilder();
        for(CloudQueueMessage retrievedMessage : iterable) {
            try {
                email = deserialize(retrievedMessage.getMessageContentAsString());
                emailService.send(email);
                sb.append(Objects.requireNonNull(email.getTo())[0].concat(" - message id: ").concat(retrievedMessage.getMessageId()).concat("\n"));
                emailQueue.deleteMessage(retrievedMessage);
            } catch (Exception e) {
                // Log exception
                log.error(e.getMessage(), e);
            }
        }
        log.info("Emails sent:\n" + sb.toString());
    }

    /**
     * Added because readValue() didn't like the "to" string array.
     * @param s - The message payload
     * @return SimpleMailMessage
     * @throws IOException Read Tree exception
     */
    private SimpleMailMessage deserialize(String s) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleMailMessage mail = new SimpleMailMessage();
        JsonNode rootNode = mapper.readTree(s);
        if (rootNode.hasNonNull("from")) {
            mail.setFrom(rootNode.path("from").asText());
        }
        if (rootNode.hasNonNull("bcc")) {
            mail.setBcc(rootNode.path("bcc").asText());
        }
        if (rootNode.hasNonNull("cc")) {
            mail.setCc(rootNode.path("cc").asText());
        }
        if (rootNode.hasNonNull("replyTo")) {
            mail.setReplyTo(rootNode.path("replyTo").asText());
        }
        if (rootNode.hasNonNull("to")) {
            JsonNode toArray = rootNode.path("to");
            for (JsonNode arrayItem : toArray) {
                mail.setTo(arrayItem.asText());
            }
        }
        if (rootNode.hasNonNull("sentDate")) {
            mail.setSentDate(new Date(rootNode.path("sentDate").asLong()));
        }
        if (rootNode.hasNonNull("subject")) {
            mail.setSubject(rootNode.path("subject").asText());
        }
        if (rootNode.hasNonNull("subject")) {
            mail.setSubject(rootNode.path("subject").asText());
        }
        if (rootNode.hasNonNull("text")) {
            mail.setText(rootNode.path("text").asText());
        }
        return mail;
    }


}
