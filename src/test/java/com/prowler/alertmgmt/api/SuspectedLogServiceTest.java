package com.prowler.alertmgmt.api;

import com.prowler.alertmgmt.model.Alert;
import com.prowler.alertmgmt.model.LogViolation;
import com.prowler.alertmgmt.model.SuspectedLog;
import com.prowler.alertmgmt.repository.AlertsRepository;
import com.prowler.alertmgmt.repository.SuspectedLogRepository;
import com.prowler.alertmgmt.vo.ViolationType;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SuspectedLogServiceTest {
    @Autowired
    SuspectedLogService service;

    @Autowired
    AlertsRepository alertsRepository;

    @Autowired
    SuspectedLogRepository repository;

    @AfterEach
    public void cleanUp() {
        repository.deleteAll();
    }

    @Test
    public void createSuspectedLog() {
        UUID logId = UUID.randomUUID();
        LocalDateTime loggedAt = LocalDateTime.now(ZoneId.of("UTC"));
        List<LogViolation> violations = Arrays.asList(LogViolation.builder().type(ViolationType.CREDIT_CARD_NUMBER).build());
        SuspectedLog log = SuspectedLog.builder().id(logId).application("app").hostName("host").loggedAt(loggedAt)
                                       .logContent("Mobile num MOB****").logFilePath("/var/application/logs/app.log").violations(violations).build();
        SuspectedLog created = service.create(log);

        Assertions.assertEquals("app", created.getApplication());
        Assertions.assertEquals("host", created.getHostName());
        Assertions.assertEquals("Mobile num MOB****", created.getLogContent());
        Assertions.assertEquals("/var/application/logs/app.log", created.getLogFilePath());
        Assertions.assertEquals(1, created.getViolations().size());
        Assertions.assertEquals(ViolationType.CREDIT_CARD_NUMBER, created.getViolations().get(0).getType());
    }

    @Test
    public void createAndGetAllSuspectedLog() {
        UUID logId = UUID.randomUUID();
        LocalDateTime loggedAt = LocalDateTime.now(ZoneId.of("UTC"));
        List<LogViolation> violations = Arrays.asList(LogViolation.builder().type(ViolationType.CREDIT_CARD_NUMBER).build());
        SuspectedLog log = SuspectedLog.builder().id(logId).application("app").hostName("host").loggedAt(loggedAt)
                                       .logContent("Mobile num MOB****").logFilePath("/var/application/logs/app.log").violations(violations).build();
        SuspectedLog created = service.create(log);

        List<SuspectedLog> logs = service.getAll();
        Assertions.assertEquals("app", logs.get(0).getApplication());
        Assertions.assertEquals("host", logs.get(0).getHostName());
        Assertions.assertEquals("Mobile num MOB****", logs.get(0).getLogContent());
    }

    @Test
    public void createSuspectedLogCreatesAlert() {
        UUID logId = UUID.randomUUID();
        LocalDateTime loggedAt = LocalDateTime.now(ZoneId.of("UTC"));
        List<LogViolation> violations = Arrays.asList(LogViolation.builder().type(ViolationType.CREDIT_CARD_NUMBER).build());
        SuspectedLog log = SuspectedLog.builder().id(logId).application("app").hostName("host").loggedAt(loggedAt)
                                       .logContent("Credit card CC****").logFilePath("/var/application/logs/app.log").violations(violations).build();
        service.create(log);

        Alert a = alertsRepository.findAlertByLogId(logId);
        Assertions.assertNotNull(a);
        Assertions.assertEquals(logId, a.getLog().getId());
    }
}
