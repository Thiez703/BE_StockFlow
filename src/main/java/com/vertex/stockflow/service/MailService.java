package com.vertex.stockflow.service;

public interface MailService {
    // Gửi email thông báo mật khẩu được tạo tự động cho người dùng.
    void sendGeneratedPassword(String toEmail, String fullName, String rawPassword);
}
