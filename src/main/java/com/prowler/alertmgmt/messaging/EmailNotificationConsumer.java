package com.prowler.alertmgmt.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prowler.alertmgmt.exception.NotificationException;
import com.prowler.alertmgmt.spi.NotificationProvider;
import com.prowler.alertmgmt.vo.EmailNotification;
import com.prowler.alertmgmt.vo.Notification;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import javax.jms.JMSException;
import javax.jms.MessageListener;

import lombok.extern.slf4j.Slf4j;

import static com.prowler.alertmgmt.util.JsonUtil.mapper;

@Component
@Slf4j
public class EmailNotificationConsumer implements MessageListener {
    @Autowired
    @Qualifier("emailProvider")
    private NotificationProvider provider;

    @Autowired
    private com.prowler.alertmgmt.config.EmailNotification emailConfig;

    @Override
    @JmsListener(destination = "${notification.email.topic}")
    public void onMessage(javax.jms.Message message) {
        try{
            ActiveMQTextMessage txtMsg = (ActiveMQTextMessage) message;
            Notification notification = mapper.readValue(txtMsg.getText(), Notification.class);
            log.info("EmailNotificationConsumer.onMessage Sending notification for alert:" + notification.getAlertId());

            sendEmail(notification);
        } catch(NotificationException | JMSException | JsonProcessingException e) {
            log.error("EmailNotificationConsumer.onMessage error occurred : "+e.getMessage(), e);
            throw new RuntimeException("EmailNotificationConsumer.onMessage error occurred : "+e.getMessage(), e);
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
