package com.prowler.alertmgmt.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prowler.alertmgmt.api.SuspectedLogService;
import com.prowler.alertmgmt.exception.LogParsingException;
import com.prowler.alertmgmt.model.LogViolation;
import com.prowler.alertmgmt.model.SuspectedLog;
import com.prowler.alertmgmt.vo.KafkaMessage;
import com.prowler.alertmgmt.vo.ViolationType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

import static com.prowler.alertmgmt.util.JsonUtil.mapper;

@Configuration
@Slf4j
public class SuspectedLogConsumer {
    private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final Pattern CREDIT_CARD_REGEX = Pattern.compile("(.*)CC\\*(.*)");
    private static final Pattern MOBILE_NUMBER_REGEX = Pattern.compile("(.*)MOB\\*(.*)");
    private static final Pattern BANK_ACCOUNT_REGEX = Pattern.compile("(.*)BNK\\*(.*)");
    private static final Map<ViolationType, Pattern> patternMap = new HashMap<>();

    static {
        patternMap.put(ViolationType.CREDIT_CARD_NUMBER, CREDIT_CARD_REGEX);
        patternMap.put(ViolationType.BANK_ACCOUNT_NUMBER, BANK_ACCOUNT_REGEX);
        patternMap.put(ViolationType.MOBILE_NUMBER, MOBILE_NUMBER_REGEX);
    }

    @Autowired
    private SuspectedLogService logService;

    @KafkaListener(topics = "prowler_alert_logs")
    public void listenGroup(String message) {
        log.debug("Received Kafka Message: " + message);
        try {
            logService.create(messageToLog(message));
        } catch (LogParsingException e) {
            //TODO
        }
    }

    private SuspectedLog messageToLog(String message) throws LogParsingException {
        SuspectedLog log = null;
        try {
            KafkaMessage msg = mapper.readValue(message, KafkaMessage.class);
            LocalDateTime loggedAt = LocalDateTime.parse(msg.getTimestamp(),
                                                         DateTimeFormatter.ofPattern(PATTERN).
                                                                 withZone(ZoneId.of("UTC")));
            List<LogViolation> violations = getViolations(msg.getMessage());
            log = SuspectedLog.builder().
                    id(UUID.randomUUID()).
                    application(msg.getProject().getApplication()).
                    hostName(msg.getProject().getHost()).
                    logFilePath(msg.getLog().getFile().getPath()).
                    logContent(msg.getMessage()).
                    loggedAt(loggedAt).
                    violations(violations).build();
        } catch (JsonProcessingException e) {
            throw new LogParsingException("Error while parsing the log received from the agent: "+message, e);
        }
        return log;
    }

    private List<LogViolation> getViolations(String message) {
        List<LogViolation> violations = new ArrayList<>();
        for (ViolationType type : patternMap.keySet()) {
            Matcher matcher = patternMap.get(type).matcher(message);
            if (matcher.matches()) {
                violations.add(LogViolation.builder().type(type).build());
            }
        }
        log.debug("Log violations for \""+message+"\" : "+violations);
        return violations;
    }
}
