package com.gcr.acm.email;

import com.gcr.acm.common.utils.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Service for sending emails.
 *
 * @author Razvan Dani
 */
@Service
@PropertySource("classpath:email-${spring.profiles.active}.properties")
public class EmailSenderService {
    @Autowired
    private EncryptionUtil encryptionUtil;

    @Value("${smtp.transport.protocol}")
    private String smtpTransportProtocol;

    @Value("${smtp.host}")
    private String smtpHost;

    @Value("${smtp.port}")
    private String smtpPort;

    @Value("${smtp.userName}")
    private String smtpUserName;

    @Value("${smtp.fromAddress}")
    private String fromAddress;

    private String smtpPassword;

    @Value("${smtp.password}")
    public void setSmtpPassword(String smtpPassword) {
        try {
            this.smtpPassword = encryptionUtil.decrypt(smtpPassword);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Value("${smtp.auth}")
    private Boolean smtpAuth;

    @Value("${smtp.ssl.enable}")
    private Boolean smtpSslEnable;

    public void sendEmail(String from, String to, String subject, String body) throws MessagingException {
        Properties props = System.getProperties();
        props.put("mail.transport.protocol", smtpTransportProtocol);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.connectiontimeout", "7000");
        props.put("mail.smtp.auth", smtpAuth);
        props.setProperty("mail.smtp.user", smtpUserName);
        props.setProperty("mail.smtp.password", smtpPassword);

        if (smtpSslEnable) {
            props.put("mail.smtp.socketFactory.port", smtpPort);
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.ssl.enable", "true");
        }

        Session session;

        if (!smtpPassword.equals("")) {
            session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpUserName, smtpPassword);
                }
            });
        } else {
            session = Session.getDefaultInstance(props);
        }

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));

        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

        msg.setSubject(subject);
        msg.setContent(body, "text/html; charset=utf-8");
        Transport.send(msg, msg.getAllRecipients());
    }

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        sendEmail(fromAddress, to, subject, body);
    }
}
