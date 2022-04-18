package com.prowler.alertmgmt.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MessageProducer {
    @Autowired
    JmsTemplate jmsTemplate;

    public void sendMessage(String destination, String msg) {
        log.debug("MessageProducer.sendMessage Sending message to Destination: "+ destination);
        jmsTemplate.convertAndSend(destination, msg);
    }
}
