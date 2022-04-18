package com.prowler.alertmgmt.api;

import com.prowler.alertmgmt.exception.LogValidationException;
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
        validateLogInput(logg);

        SuspectedLog created = repository.save(logg);
        //Alert alert = Alert.builder().logId(created.getId()).log(created).status(AlertStatus.CREATED).build();
        Alert alert = Alert.builder().log(created).status(AlertStatus.CREATED).build();
        alertsService.createAlert(alert);
        return created;
    }

    private void validateLogInput(SuspectedLog log) {
        if (log.getApplication() == null || log.getApplication().isBlank()) {
            throw new LogValidationException("Application is not specified in the log with the following content: "+
                                                     log.getLogContent());
        }
        if (log.getHostName() == null || log.getHostName().isBlank()) {
            throw new LogValidationException("Hostname is not specified in the log with the following content: "+
                                                     log.getLogContent());
        }
        if (log.getLogFilePath() == null || log.getLogFilePath().isBlank()) {
            throw new LogValidationException("Log file path is not specified in the log with the following content: "+
                                                     log.getLogContent());
        }
        if (log.getLogContent() == null || log.getLogContent().isBlank()) {
            throw new LogValidationException("Log content is not specified in the log!!");
        }
        if (!hasViolations(log)) {
            throw new LogValidationException("No violations found in the suspected log with the following content: "+
                                                     log.getLogContent());
        }
    }

    public List<SuspectedLog> getAll() {
        List<SuspectedLog> logs = new ArrayList<>();
        repository.findAll().forEach(logs::add);
        return logs;
    }

    private boolean hasViolations(SuspectedLog logg) {
        return logg.getViolations()!= null && !logg.getViolations().isEmpty();
    }
}
