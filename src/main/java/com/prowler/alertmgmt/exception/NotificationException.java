package com.prowler.alertmgmt.exception;

import javax.mail.MessagingException;

public class NotificationException extends Exception {
    public NotificationException(String s, Throwable e) {
        super(s, e);
    }
}
