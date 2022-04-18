package com.prowler.alertmgmt.api;

import com.prowler.alertmgmt.model.Alert;
import com.prowler.alertmgmt.model.LogViolation;
import com.prowler.alertmgmt.model.SuspectedLog;
import com.prowler.alertmgmt.vo.AlertStatus;
import com.prowler.alertmgmt.vo.ViolationType;

import org.junit.FixMethodOrder;
import org.junit.Test;
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
public class AlertServiceTest {
    @Autowired
    AlertsService service;

    @Test
    public void testCreateAndGetAlert() {
        UUID logId = UUID.randomUUID();
        LocalDateTime loggedAt = LocalDateTime.now(ZoneId.of("UTC"));
        List<LogViolation> violations =
                Arrays.asList(LogViolation.builder().type(ViolationType.CREDIT_CARD_NUMBER).build());
        SuspectedLog log = SuspectedLog.builder().id(logId).application("app").hostName("host").
                logContent("Mobile num MOB****").loggedAt(loggedAt).violations(violations).build();
        Alert alert = service.createAlert(Alert.builder().status(AlertStatus.CREATED).log(log).build());

        Assertions.assertEquals(logId, alert.getLog().getId());
        Assertions.assertEquals("app", alert.getLog().getApplication());
        Assertions.assertEquals("host", alert.getLog().getHostName());
        Assertions.assertEquals("Mobile num MOB****", alert.getLog().getLogContent());
        Assertions.assertEquals("Mobile num MOB****", alert.getLog().getLogContent());
        Assertions.assertEquals(ViolationType.CREDIT_CARD_NUMBER, alert.getLog().getViolations().get(0).getType());
    }

    @Test
    public void testCreateAndGetAlertWithMultipleViolations() {
        UUID logId = UUID.randomUUID();
        LocalDateTime loggedAt = LocalDateTime.now(ZoneId.of("UTC"));
        List<LogViolation> violations =
                Arrays.asList(
                        LogViolation.builder().type(ViolationType.CREDIT_CARD_NUMBER).build(),
                        LogViolation.builder().type(ViolationType.MOBILE_NUMBER).build()
                );
        SuspectedLog log = SuspectedLog.builder().id(logId).application("app").hostName("host").
                logContent("Mobile num MOB**** credit card CC***").loggedAt(loggedAt).violations(violations).build();
        Alert alert = service.createAlert(Alert.builder().status(AlertStatus.CREATED).log(log).build());

        Assertions.assertEquals(logId, alert.getLog().getId());
        Assertions.assertEquals("app", alert.getLog().getApplication());
        Assertions.assertEquals("host", alert.getLog().getHostName());
        Assertions.assertEquals("Mobile num MOB**** credit card CC***", alert.getLog().getLogContent());
        Assertions.assertEquals(2, alert.getLog().getViolations().size());
        Assertions.assertEquals(ViolationType.CREDIT_CARD_NUMBER, alert.getLog().getViolations().get(0).getType());
        Assertions.assertEquals(ViolationType.MOBILE_NUMBER, alert.getLog().getViolations().get(1).getType());
    }
}
