package com.prowler.alertmgmt.controller;

import com.prowler.alertmgmt.model.Alert;
import com.prowler.alertmgmt.model.SuspectedLog;
import com.prowler.alertmgmt.repository.AlertsRepository;
import com.prowler.alertmgmt.repository.SuspectedLogRepository;
import com.prowler.alertmgmt.vo.AlertStatus;

import org.hamcrest.Matchers;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AlertsControllerIntgTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private AlertsRepository repository;
    @Autowired
    private SuspectedLogRepository logRepository;

    @AfterEach
    private void cleanUp() {
        repository.deleteAll();
    }

    @Test
    public void createAndGetAlertReturnsStatus200()
            throws Exception {
        UUID logId = UUID.randomUUID();
        LocalDateTime loggedAt = LocalDateTime.now(ZoneId.of("UTC"));
        SuspectedLog log = logRepository.save(SuspectedLog.builder().id(logId).application("app").hostName("host").
                logContent("Mobile num MOB****").loggedAt(loggedAt).build());
        Alert alert = repository.save(Alert.builder().status(AlertStatus.CREATED).log(log).build());

        mvc.perform(MockMvcRequestBuilders.get("/prowler/v1/alerts/"+alert.getId())
                                          .contentType(MediaType.APPLICATION_JSON))
           .andExpect(MockMvcResultMatchers.status().isOk())
           .andExpect(MockMvcResultMatchers.content()
                              .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
           .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(alert.getId().toString()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.log.id").value(logId.toString()))
           .andExpect(MockMvcResultMatchers.jsonPath("$.log.application").value("app"))
           .andExpect(MockMvcResultMatchers.jsonPath("$.log.hostName").value("host"))
           .andExpect(MockMvcResultMatchers.jsonPath("$.log.logContent").value("Mobile num MOB****"))
           .andExpect(MockMvcResultMatchers.jsonPath("$.log.loggedAt").value(loggedAt.toString()))
           .andReturn();
    }

    @Test
    public void getAlertWithRandomUUIDReturnsStatus404()
            throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/prowler/v1/alerts/"+UUID.randomUUID().toString())
                                          .contentType(MediaType.APPLICATION_JSON))
           .andExpect(MockMvcResultMatchers.status().isNotFound())
           .andReturn();
    }

    @Test
    public void createAndSearchAlertsReturnsAlert()
            throws Exception {
        UUID logId = UUID.randomUUID();
        LocalDateTime loggedAt = LocalDateTime.now(ZoneId.of("UTC"));
        SuspectedLog log = logRepository.save(SuspectedLog.builder().id(logId).application("app").hostName("host").
                logContent("Mobile num MOB****").loggedAt(loggedAt).build());
        Alert alert = repository.save(Alert.builder().status(AlertStatus.CREATED).log(log).build());

        mvc.perform(MockMvcRequestBuilders.get("/prowler/v1/alerts?application=app")
                                          .contentType(MediaType.APPLICATION_JSON))
           .andExpect(MockMvcResultMatchers.status().isOk())
           .andExpect(MockMvcResultMatchers.content()
                                           .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
           .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(alert.getId().toString()))
           .andExpect(MockMvcResultMatchers.jsonPath("$[0].log.id").value(logId.toString()))
           .andExpect(MockMvcResultMatchers.jsonPath("$[0].log.application").value("app"))
           .andExpect(MockMvcResultMatchers.jsonPath("$[0].log.hostName").value("host"))
           .andExpect(MockMvcResultMatchers.jsonPath("$[0].log.logContent").value("Mobile num MOB****"))
           .andReturn();
    }

    @Test
    public void createAndSearchAlertsAfterLoggedAtReturnsEmptyResult()
            throws Exception {
        UUID logId = UUID.randomUUID();
        LocalDateTime loggedAt = LocalDateTime.now(ZoneId.of("UTC"));
        SuspectedLog log = logRepository.save(SuspectedLog.builder().id(logId).application("app").hostName("host").
                logContent("Mobile num MOB****").loggedAt(loggedAt).build());
        Alert alert = repository.save(Alert.builder().status(AlertStatus.CREATED).log(log).build());

        String startTime = loggedAt.plusSeconds(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        mvc.perform(MockMvcRequestBuilders.get("/prowler/v1/alerts?application=app&startTime="+startTime)
                                          .contentType(MediaType.APPLICATION_JSON))
           .andExpect(MockMvcResultMatchers.status().isOk())
           .andExpect(MockMvcResultMatchers.content()
                                           .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
           .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(0)))
           .andReturn();
    }

    @Test
    public void createTwoAndSearchAlertsWithStartTimeReturnsTwo()
            throws Exception {
        UUID logId = UUID.randomUUID();
        LocalDateTime loggedAt = LocalDateTime.now(ZoneId.of("UTC")).minusMinutes(5);
        SuspectedLog log = logRepository.save(SuspectedLog.builder().id(logId).application("app").hostName("host").
                logContent("Mobile num MOB****").loggedAt(loggedAt).build());
        Alert alert1 = repository.save(Alert.builder().status(AlertStatus.CREATED).log(log).build());

        UUID logId2 = UUID.randomUUID();
        LocalDateTime loggedAt2 = LocalDateTime.now(ZoneId.of("UTC")).minusMinutes(5);
        SuspectedLog log2 = logRepository.save(SuspectedLog.builder().id(logId2).application("app").hostName("host").
                logContent("Mobile num MOB****").loggedAt(loggedAt2).build());
        Alert alert2 = repository.save(Alert.builder().status(AlertStatus.CREATED).log(log2).build());

        String startTime = loggedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endTime = loggedAt2.plusSeconds(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        mvc.perform(MockMvcRequestBuilders.get("/prowler/v1/alerts?application=app&startTime="+startTime+"&endTime="+endTime)
                                          .contentType(MediaType.APPLICATION_JSON))
           .andExpect(MockMvcResultMatchers.status().isOk())
           .andExpect(MockMvcResultMatchers.content()
                                           .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
           .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
           .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(alert2.getId().toString()))
           .andExpect(MockMvcResultMatchers.jsonPath("$[0].log.id").value(logId2.toString()))
           .andExpect(MockMvcResultMatchers.jsonPath("$[0].log.application").value("app"))
           .andExpect(MockMvcResultMatchers.jsonPath("$[0].log.hostName").value("host"))
           .andExpect(MockMvcResultMatchers.jsonPath("$[0].log.logContent").value("Mobile num MOB****"))
           .andReturn();
    }

    @Test
    public void createTwoAndSearchAlertsWithStartAndEndTimeReturnsOne()
            throws Exception {
        UUID logId = UUID.randomUUID();
        LocalDateTime loggedAt = LocalDateTime.now(ZoneId.of("UTC"));
        SuspectedLog log = logRepository.save(SuspectedLog.builder().id(logId).application("app").hostName("host").
                logContent("Mobile num MOB****").loggedAt(loggedAt).build());
        Alert alert1 = repository.save(Alert.builder().status(AlertStatus.CREATED).log(log).build());

        Thread.sleep(1000);
        UUID logId2 = UUID.randomUUID();
        LocalDateTime loggedAt2 = LocalDateTime.now(ZoneId.of("UTC"));
        SuspectedLog log2 = logRepository.save(SuspectedLog.builder().id(logId2).application("app").hostName("host").
                logContent("Mobile num MOB****").loggedAt(loggedAt2).build());
        Alert alert2 = repository.save(Alert.builder().status(AlertStatus.CREATED).log(log2).build());

        String startTime = loggedAt2.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        mvc.perform(MockMvcRequestBuilders.get("/prowler/v1/alerts?application=app&startTime="+startTime)
                                          .contentType(MediaType.APPLICATION_JSON))
           .andExpect(MockMvcResultMatchers.status().isOk())
           .andExpect(MockMvcResultMatchers.content()
                                           .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
           .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
           .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(alert2.getId().toString()))
           .andExpect(MockMvcResultMatchers.jsonPath("$[0].log.id").value(logId2.toString()))
           .andExpect(MockMvcResultMatchers.jsonPath("$[0].log.application").value("app"))
           .andExpect(MockMvcResultMatchers.jsonPath("$[0].log.hostName").value("host"))
           .andExpect(MockMvcResultMatchers.jsonPath("$[0].log.logContent").value("Mobile num MOB****"))
           .andReturn();
    }

    @Test
    public void createMultipleAndSearchAlertsReturnsTwentyByDefault()
            throws Exception {
        UUID logId = UUID.randomUUID();
        LocalDateTime loggedAt = LocalDateTime.now(ZoneId.of("UTC"));
        for (int i = 0; i < 30; i++) {
            SuspectedLog log = logRepository.save(SuspectedLog.builder().id(logId).application("app").hostName("host").
                    logContent("Mobile num MOB****").loggedAt(loggedAt).build());
            Alert alert = repository.save(Alert.builder().status(AlertStatus.CREATED).log(log).build());
        }

        String startTime = loggedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        mvc.perform(MockMvcRequestBuilders.get("/prowler/v1/alerts?application=app&startTime="+startTime)
                                          .contentType(MediaType.APPLICATION_JSON))
           .andExpect(MockMvcResultMatchers.status().isOk())
           .andExpect(MockMvcResultMatchers.content()
                                           .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
           .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(20)))
           .andReturn();
    }

    @Test
    public void createMultipleAndSearchAlertsHonoursLimit()
            throws Exception {
        UUID logId = UUID.randomUUID();
        LocalDateTime loggedAt = LocalDateTime.now(ZoneId.of("UTC"));
        for (int i = 0; i < 30; i++) {
            SuspectedLog log = logRepository.save(SuspectedLog.builder().id(logId).application("app").hostName("host").
                    logContent("Mobile num MOB****").loggedAt(loggedAt).build());
            Alert alert = repository.save(Alert.builder().status(AlertStatus.CREATED).log(log).build());
        }

        String startTime = loggedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        mvc.perform(MockMvcRequestBuilders.get("/prowler/v1/alerts?application=app&startTime="+startTime+"&limit="+25)
                                          .contentType(MediaType.APPLICATION_JSON))
           .andExpect(MockMvcResultMatchers.status().isOk())
           .andExpect(MockMvcResultMatchers.content()
                                           .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
           .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(25)))
           .andReturn();
    }

    @Test
    public void createMultipleAndSearchAlertsWithoutApplicationReturns400()
            throws Exception {
        UUID logId = UUID.randomUUID();
        LocalDateTime loggedAt = LocalDateTime.now(ZoneId.of("UTC"));
        for (int i = 0; i < 30; i++) {
            SuspectedLog log = logRepository.save(SuspectedLog.builder().id(logId).application("app").hostName("host").
                    logContent("Mobile num MOB****").loggedAt(loggedAt).build());
            Alert alert = repository.save(Alert.builder().status(AlertStatus.CREATED).log(log).build());
        }

        mvc.perform(MockMvcRequestBuilders.get("/prowler/v1/alerts")
                                          .contentType(MediaType.APPLICATION_JSON))
           .andExpect(MockMvcResultMatchers.status().is4xxClientError())
           .andReturn();
    }
}
