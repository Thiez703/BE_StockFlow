package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.LoginRequest;
import com.vertex.stockflow.dto.response.LoginResponse;
import com.vertex.stockflow.dto.response.UserResponse;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    UserResponse getCurrentUser(String email );
}
