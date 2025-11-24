package com.example.webchat.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendMail(String toEmail, String OTP ){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setSubject("YOUR OTP FOR ACCOUNT CREATION");
        message.setText("Your OTP is: "+ OTP);
        message.setTo(toEmail);
        mailSender.send(message);
        System.out.println("Mail sent Successfully");
    }
}
