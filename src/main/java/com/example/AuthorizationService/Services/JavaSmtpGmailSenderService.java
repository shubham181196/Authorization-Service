package com.example.AuthorizationService.Services;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;


@Service

@AllArgsConstructor
@NoArgsConstructor
public class JavaSmtpGmailSenderService{
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    EmailHelper emailHelper;


//    @Async
    public void sendEmail(String toEmail,String subject,String body){
        emailHelper.submitEmailTask(()->{
            SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
            simpleMailMessage.setFrom("marcusaurelius7077@gmail.com");
            simpleMailMessage.setTo(toEmail);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(body);
            javaMailSender.send(simpleMailMessage);
        });

    }

}
