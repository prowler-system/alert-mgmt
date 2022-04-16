package com.prowler.alertmgmt.api;

import com.prowler.alertmgmt.model.Alert;
import com.prowler.alertmgmt.model.LogViolation;
import com.prowler.alertmgmt.model.SuspectedLog;
import com.prowler.alertmgmt.repository.SuspectedLogRepository;
import com.prowler.alertmgmt.vo.AlertStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SuspectedLogService {
    @Autowired
    private SuspectedLogRepository repository;

    @Autowired
    private AlertsService alertsService;

    public SuspectedLog addSuspectedLog(SuspectedLog logg) {
        if (!hasViolations(logg)) {
            log.warn("No violations found in the suspected log with the following content: "+logg.getLogContent());
            return null;
        }
        SuspectedLog created = repository.save(logg);

        log.debug("Created log with violation type: "+created.getViolations().get(0).getType());
        //create an alert for the suspected log so that they can be triggered.
        //For now, Alerts is part of the same service, so directly calling the API
        /*List<Alert> alerts = created.getViolations().stream().
                map(v -> Alert.builder().
                        id(UUID.randomUUID()).
                        logViolationId(v.getId()).
                          logId(created.getId()).
                          status(AlertStatus.CREATED).
                          build()).
                            collect(Collectors.toList());*/

        //Alert alert = Alert.builder().logId(created.getId()).log(created).status(AlertStatus.CREATED).build();
        Alert alert = Alert.builder().log(created).status(AlertStatus.CREATED).build();
        alertsService.createAlert(alert);
        return created;
    }

    private boolean hasViolations(SuspectedLog logg) {
        return !logg.getViolations().isEmpty();
    }

    public List<SuspectedLog> getAllSuspectedLogs() {
        List<SuspectedLog> logs = new ArrayList<>();
        repository.findAll().forEach(logs::add);
        return logs;
    }
}
