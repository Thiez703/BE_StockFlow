package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.service.MailService;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
//Để bấm nút gửi qua email
@Service
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;

    public MailServiceImpl(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    @Override
    public void sendGeneratedPassword(String toEmail, String fullName, String rawPassword) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject("StockFlow - Tài khoản của bạn");
        msg.setText("Xin chào " + fullName + ",\n\n"
                + "Tài khoản của bạn đã được cấp/đặt lại mật khẩu.\n"
                + "Mật khẩu đăng nhập: " + rawPassword + "\n\n"
                + "Bạn sẽ được yêu cầu đổi mật khẩu ngay khi đăng nhập lần đầu.");
        mailSender.send(msg);
    }
}
