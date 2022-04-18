package com.prowler.alertmgmt.vo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prowler.alertmgmt.model.Alert;
import com.prowler.alertmgmt.model.SuspectedLog;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Notification {
    private static final String SUBJECT_FORMAT = "URGENT: Security violation detected in the logs for Application %s";
    private String subject;
    private String        body;
    private LocalDateTime sentTime;
    private String application;
    private UUID   alertId;

    public static Notification from(Alert alert) {
        SuspectedLog log = alert.getLog();
        String alertPayload = "";
        try {
            alertPayload = log.toJson();
        } catch (JsonProcessingException e) {
            //TODO
        }
        return Notification.builder().application(log.getApplication()).sentTime(LocalDateTime.now()).
                alertId(alert.getId()).subject(String.format(SUBJECT_FORMAT, log.getApplication())).body(alertPayload).
                                   build();
    }

    public String getSubject() {
        return String.format(SUBJECT_FORMAT, application);
    }
}
