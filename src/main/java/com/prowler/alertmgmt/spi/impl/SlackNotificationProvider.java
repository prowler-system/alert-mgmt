package com.prowler.alertmgmt.spi.impl;

import com.prowler.alertmgmt.spi.NotificationProvider;
import com.prowler.alertmgmt.vo.Notification;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component("slackProvider")
public class SlackNotificationProvider implements NotificationProvider {
    @Override
    public void notify(Notification event) {

    }
}
