package com.prowler.alertmgmt.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Data @SuperBuilder
public class SlackNotification extends Notification{
    private String slackChannel;
}
