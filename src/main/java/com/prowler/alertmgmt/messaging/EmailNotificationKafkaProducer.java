package com.prowler.alertmgmt.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmailNotificationKafkaProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topicName, String msg) {
        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(topicName, msg);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.debug("Sent message=[" + msg +"] with offset=[" + result.getRecordMetadata().offset() + "]");
            }
            @Override
            public void onFailure(Throwable e) {
                log.error("Unable to send message=["+ msg + "] due to: " + e.getMessage(), e);
            }
        });
    }
}
