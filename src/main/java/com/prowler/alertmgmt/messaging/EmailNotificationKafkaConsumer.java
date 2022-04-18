package com.prowler.alertmgmt.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prowler.alertmgmt.exception.NotificationException;
import com.prowler.alertmgmt.spi.NotificationProvider;
import com.prowler.alertmgmt.vo.EmailNotification;
import com.prowler.alertmgmt.vo.Notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;

import static com.prowler.alertmgmt.util.JsonUtil.mapper;

@Configuration
@Slf4j
public class EmailNotificationKafkaConsumer {

    @Autowired
    @Qualifier("emailProvider")
    private NotificationProvider provider;

    @Autowired
    private com.prowler.alertmgmt.config.EmailNotification emailConfig;

    @KafkaListener(topics = "${notification.email.topic}")
    public void listenGroup(String message) {
        log.debug("EmailNotificationKafkaConsumer Received Message: " + message);
        try {
            Notification notification = mapper.readValue(message, Notification.class);
            log.info("EmailNotificationKafkaConsumer Sending notification for alert:" + notification.getAlertId());

            sendEmail(notification);
        } catch (JsonProcessingException | NotificationException e) {
            log.error("EmailNotificationKafkaConsumer error occurred:"+e.getMessage(), e);
            throw new RuntimeException("EmailNotificationKafkaConsumer error occurred : "+e.getMessage(), e);
        }
    }

    private void sendEmail(Notification notification) throws NotificationException {
        provider.notify(EmailNotification.builder().
                fromEmailId(emailConfig.getFromAddress()).
                                                 toEmailId(emailConfig.getToAddress()).
                                                 sentTime(LocalDateTime.now()).
                                                 subject(notification.getSubject()).
                                                 body(getBody(notification)).
                                                 build());
    }

    private String getBody(Notification notification) {
        return "Hi Team,\n\nImmediate action is required for the following Security violation detected in Application" +
                " Logs:\n\n" + notification.getBody() + "\n\nCheers!\nProwler Team";
    }
}
