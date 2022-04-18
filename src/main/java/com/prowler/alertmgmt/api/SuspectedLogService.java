package com.prowler.alertmgmt.api;

import com.prowler.alertmgmt.model.Alert;
import com.prowler.alertmgmt.model.SuspectedLog;
import com.prowler.alertmgmt.repository.SuspectedLogRepository;
import com.prowler.alertmgmt.vo.AlertStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SuspectedLogService {
    @Autowired
    private SuspectedLogRepository repository;

    @Autowired
    private AlertsService alertsService;

    public SuspectedLog create(SuspectedLog logg) {
        if (!hasViolations(logg)) {
            log.warn("No violations found in the suspected log with the following content: "+logg.getLogContent());
            return null;
        }
        SuspectedLog created = repository.save(logg);
        //Alert alert = Alert.builder().logId(created.getId()).log(created).status(AlertStatus.CREATED).build();
        Alert alert = Alert.builder().log(created).status(AlertStatus.CREATED).build();
        alertsService.createAlert(alert);
        return created;
    }

    public List<SuspectedLog> getAll() {
        List<SuspectedLog> logs = new ArrayList<>();
        repository.findAll().forEach(logs::add);
        return logs;
    }

    private boolean hasViolations(SuspectedLog logg) {
        return !logg.getViolations().isEmpty();
    }
}
