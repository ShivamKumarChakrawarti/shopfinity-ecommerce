package com.luxora.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    public void sendVerificationOtpEmail(String userEmail, String otp, String subject, String text) throws MessagingException {

        if (userEmail == null || userEmail.isEmpty()) {
            throw new IllegalArgumentException("User email is empty or null");
        }

        try{
            MimeMessage mimeMessage=javaMailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(userEmail);
            helper.setSubject(subject);
            helper.setText(text, true); // true for HTML content
            helper.setFrom("luxorashop28@gmail.com");

            javaMailSender.send(mimeMessage);

        }catch (MessagingException | MailException e){
            System.out.println("Failed to send email to: " + userEmail);
            e.printStackTrace();
            throw new MailSendException("failed to send email " +e.getMessage());

        }
    }
}
