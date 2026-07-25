package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.LoginRequest;
import com.vertex.stockflow.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
}
