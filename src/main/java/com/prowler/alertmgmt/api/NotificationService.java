package com.prowler.alertmgmt.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prowler.alertmgmt.config.EmailNotification;
import com.prowler.alertmgmt.messaging.MessageProducer;
import com.prowler.alertmgmt.messaging.EmailNotificationKafkaProducer;
import com.prowler.alertmgmt.vo.Notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import static com.prowler.alertmgmt.util.JsonUtil.mapper;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    private EmailNotification emailConfig;

    @Autowired
    private MessageProducer producer;

    @Autowired
    private EmailNotificationKafkaProducer kafkaProducer;

    public boolean send(Notification notification) {
        try {
            //producer.sendMessage(emailConfig.getTopic(), toMessage(notification));
            kafkaProducer.sendMessage(emailConfig.getTopic(), toMessage(notification));
        } catch (JmsException e) {
            log.error("NotificationService.send error while sending notification for alert "+notification.getAlertId()+
                    ": "+e.getMessage(), e);
            return false;
        }
        return true;
    }

    private String toMessage(Notification notification) {
        String msgJson = null;
        try {
            msgJson = mapper.writeValueAsString(notification);
        } catch (JsonProcessingException e) {
            log.error("NotificationService error while serializing notification: "+e.getMessage(), e);
            throw new IllegalArgumentException("NotificationService Unexpected error while serializing notification: "+
                                                       e.getMessage(), e);
        }
        return msgJson;
    }
}
