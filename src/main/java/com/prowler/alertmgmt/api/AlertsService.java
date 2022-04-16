package com.prowler.alertmgmt.api;

import com.prowler.alertmgmt.model.Alert;
import com.prowler.alertmgmt.repository.AlertsRepository;
import com.prowler.alertmgmt.repository.SuspectedLogRepository;
import com.prowler.alertmgmt.vo.AlertStatus;
import com.prowler.alertmgmt.vo.Notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AlertsService {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AlertsRepository repository;

    @Autowired
    private SuspectedLogRepository logRepository;

    public void createAlert(Alert alert) {
        //log.info("Original alert UUID is >>> "+alert.getId().toString()+", "+alert.getLogId().toString());
        Alert a = repository.save(alert);
        boolean sent = notificationService.send(Notification.from(alert));
        if (sent) {
            a.setStatus(AlertStatus.NOTIFIED);
        } else {
            a.setStatus(AlertStatus.NOTIFY_FAILED);
        }
        //log.info("Alert UUID is >>> "+a.getId().toString()+", "+alert.getLogId().toString());
        repository.save(a);
    }

    public List<Alert> searchAlerts(String application, String host, LocalDateTime startTime,
                                    LocalDateTime endTime, Integer offset, Integer limit) {
        log.info(application+"; "+host+"; "+startTime+"; "+endTime+"; "+offset+"; "+limit);
        if (startTime == null) {
            startTime = LocalDateTime.now(ZoneId.of("UTC")).minusHours(2);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now(ZoneId.of("UTC"));
        }
        if (offset == null) {
            offset = 1;
        }
        if (limit == null) {
            limit = 20;
        }

        List<Alert> alerts = null;
        if (host == null) {
            alerts = repository.search(application, startTime, endTime, offset, limit);
        } else {
            alerts = repository.search(application, host, startTime, endTime, offset, limit);
        }
        return alerts;
    }
}
