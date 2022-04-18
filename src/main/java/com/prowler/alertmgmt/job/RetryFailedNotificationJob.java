package com.prowler.alertmgmt.job;

import com.prowler.alertmgmt.api.AlertsService;
import com.prowler.alertmgmt.model.Alert;
import com.prowler.alertmgmt.repository.AlertsRepository;
import com.prowler.alertmgmt.vo.AlertStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RetryFailedNotificationJob {
    private static final int BATCH_SIZE = 10;

    @Autowired
    AlertsRepository alertsRepository;

    @Autowired
    AlertsService alertsService;

    @Scheduled(fixedRate = 30000)
    @Timed(value = "retryFailedNotifications.time", description = "Time taken to fetch alerts in NOTIFY_FAILED status")
    public void retryFailedNotifications() {
        log.debug("RetryFailedNotificationJob.start");
        int retriedBatches = 0;
        //Processes max 500 msgs per job run
        while (retriedBatches < 50) {
            List<Alert> failedAlerts = alertsRepository.findAlertsByStatus(AlertStatus.NOTIFY_FAILED.toString(),
                                                                           BATCH_SIZE);
            if (failedAlerts.isEmpty()) {
                log.debug("RetryFailedNotificationJob.exiting No failures to retry");
                return;
            }
            log.info("RetryFailedNotificationJob.retryFailedNotifications retrying " +
                             failedAlerts.stream().map(a -> a.getId()).collect(Collectors.toList()));
            failedAlerts.forEach(a -> alertsService.sendNotifications(a));
            retriedBatches++;
        }
    }
}
