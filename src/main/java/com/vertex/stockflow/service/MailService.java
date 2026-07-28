package com.vertex.stockflow.service;

public interface MailService {
    void sendGeneratedPassword(String toEmail, String fullName, String rawPassword);
}
