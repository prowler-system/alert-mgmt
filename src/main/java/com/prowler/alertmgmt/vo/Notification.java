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
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Slf4j
public class Notification {
    private static final String SUBJECT_FORMAT = "URGENT: Security violation detected in the logs for Application %s";
    private String subject;
    private String        body;
    private LocalDateTime sentTime;
    private String application;
    private UUID   alertId;

    public static Notification from(Alert alert) {
        SuspectedLog logg = alert.getLog();
        String alertPayload = "";
        try {
            alertPayload = logg.toJson();
        } catch (JsonProcessingException e) {
            log.error("Unexpected error occurred while serializing suspected log: "+e.getMessage(), e);
        }
        return Notification.builder().application(logg.getApplication()).sentTime(LocalDateTime.now()).
                alertId(alert.getId()).subject(String.format(SUBJECT_FORMAT, logg.getApplication())).body(alertPayload).
                                   build();
    }
}
