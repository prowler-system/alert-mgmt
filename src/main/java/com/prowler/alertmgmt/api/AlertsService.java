package com.prowler.alertmgmt.api;

import com.prowler.alertmgmt.exception.AlertNotFoundExeption;
import com.prowler.alertmgmt.model.Alert;
import com.prowler.alertmgmt.repository.AlertsRepository;
import com.prowler.alertmgmt.vo.AlertStatus;
import com.prowler.alertmgmt.vo.Notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AlertsService {
    private static final Integer DEFAULT_OFFSET = 0;
    private static final Integer DEFAULT_LIMIT = 20;
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AlertsRepository repository;

    @Timed(value = "createAlert.time", description = "Time taken to create an alert")
    public Alert createAlert(Alert alert) {
        Alert created = repository.save(alert);
        sendNotifications(created);
        return created;
    }

    public void sendNotifications(Alert alert) {
        boolean sent = notificationService.send(Notification.from(alert));
        if (sent) {
            alert.setStatus(AlertStatus.NOTIFIED);
        } else {
            alert.setStatus(AlertStatus.NOTIFY_FAILED);
        }
        repository.save(alert);
    }

    @Timed(value = "getAlert.time", description = "Time taken to fetch an alert")
    public Alert getAlert(UUID id) {
        return repository.findById(id).orElseThrow(AlertNotFoundExeption::new);
    }

    @Timed(value = "searchAlerts.time", description = "Time taken to search alerts")
    public List<Alert> searchAlerts(String application, String host, LocalDateTime startTime,
                                    LocalDateTime endTime, Integer offset, Integer limit) {
        startTime = Optional.ofNullable(startTime).orElse(LocalDateTime.now(ZoneId.of("UTC")).minusHours(2));
        endTime = Optional.ofNullable(endTime).orElse(LocalDateTime.now(ZoneId.of("UTC")));
        offset = Optional.ofNullable(offset).orElse(DEFAULT_OFFSET);
        limit = Optional.ofNullable(limit).orElse(DEFAULT_LIMIT);

        if (log.isDebugEnabled())
            log.debug("Searching alerts for criteria: "+application+";"+host+";"+startTime+";"+endTime+";"+offset+";"+limit);

        List<Alert> alerts = null;
        if (host == null) {
            alerts = repository.search(application, startTime, endTime, offset, limit);
        } else {
            alerts = repository.search(application, host, startTime, endTime, offset, limit);
        }
        return alerts;
    }
}
