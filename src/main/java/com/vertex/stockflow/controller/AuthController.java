package com.vertex.stockflow.controller;

import com.vertex.stockflow.dto.request.LoginRequest;
import com.vertex.stockflow.dto.response.LoginResponse;
import com.vertex.stockflow.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

        private final AuthService authService;

        @PostMapping("/login")
        public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        }
}
