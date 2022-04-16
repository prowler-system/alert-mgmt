package com.prowler.alertmgmt.vo;

import org.springframework.beans.factory.annotation.Value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Data @SuperBuilder
public class EmailNotification extends Notification{
    @Value("${notification.email.from-address}")
    private final String fromEmailId;

    @Value("${notification.email.to-address}")
    private final String toEmailId;
}
