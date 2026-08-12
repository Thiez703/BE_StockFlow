package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.ChangePasswordRequest;
import com.vertex.stockflow.dto.request.LoginRequest;
import com.vertex.stockflow.dto.response.LoginResponse;
import com.vertex.stockflow.dto.response.UserResponse;
import org.springframework.security.core.userdetails.User;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    UserResponse getCurrentUser(String email);
    void changePassword(ChangePasswordRequest request, User actor);
}
