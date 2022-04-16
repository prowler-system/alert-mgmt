package com.prowler.alertmgmt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties("notification.email")
@Getter @Setter
public class EmailNotification {
    private String topic;
    private String smtpHost;
    private String smtpPort;
    private String fromAddress;
    private String toAddress;
}
