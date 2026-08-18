package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.ChangePasswordRequest;
import com.vertex.stockflow.dto.request.LoginRequest;
import com.vertex.stockflow.dto.response.LoginResponse;
import com.vertex.stockflow.dto.response.UserResponse;
import org.springframework.security.core.userdetails.User;

public interface AuthService {
    // Đăng nhập hệ thống.
    LoginResponse login(LoginRequest loginRequest);

    // Lấy thông tin người dùng hiện hành theo email.
    UserResponse getCurrentUser(String email);

    // Đổi mật khẩu người dùng.
    void changePassword(ChangePasswordRequest request, User actor);
}
