package com.prowler.alertmgmt.spi;

import com.prowler.alertmgmt.exception.NotificationException;
import com.prowler.alertmgmt.vo.Notification;
import org.springframework.stereotype.Service;

@Service
public interface NotificationProvider {
    public void notify(Notification event) throws NotificationException;
}
