package com.realestatebackend.common;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mail;
    public void send(String to, String subject, String text){
        var m = new SimpleMailMessage();
        m.setTo(to); m.setSubject(subject); m.setText(text);
        mail.send(m);
    }
}
