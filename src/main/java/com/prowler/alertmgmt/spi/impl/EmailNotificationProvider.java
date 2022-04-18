package com.prowler.alertmgmt.spi.impl;

import com.prowler.alertmgmt.exception.NotificationException;
import com.prowler.alertmgmt.spi.NotificationProvider;
import com.prowler.alertmgmt.vo.EmailNotification;
import com.prowler.alertmgmt.vo.Notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import lombok.extern.slf4j.Slf4j;

@Component("emailProvider")
@Slf4j
public class EmailNotificationProvider implements NotificationProvider {

    @Autowired
    private com.prowler.alertmgmt.config.EmailNotification emailConfig;

    @Override
    public void notify(Notification event) throws NotificationException {
        EmailNotification emailNotification = (EmailNotification) event;
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", emailConfig.getSmtpHost());
        props.put("mail.smtp.port", emailConfig.getSmtpPort());
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailConfig.getFromAddress(), "Prowler@123");
            }
        });

        try{
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailConfig.getFromAddress()));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailConfig.getToAddress()));
            message.setSubject(emailNotification.getSubject());
            message.setText(emailNotification.getBody());

            Transport.send(message);
            log.debug("Email sent successfully.");
        } catch (MessagingException mex) {
            log.error("An error occurred while sending email notification for alert", mex);
            throw new NotificationException("An error occurred while sending email notification for alert", mex);
        }
    }
}
