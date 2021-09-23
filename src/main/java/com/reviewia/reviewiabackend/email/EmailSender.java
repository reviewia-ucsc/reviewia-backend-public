package com.reviewia.reviewiabackend.email;

import javax.mail.MessagingException;

public interface EmailSender {
    void send(String to, String from, String subject, String content) throws MessagingException;
}
