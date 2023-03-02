package com.example.demo.email;

public interface EmailSenderService {
    void sendEmail(String to, String subject, String message);
}
