package com.vertex.stockflow.controller;

import com.vertex.stockflow.dto.request.LoginRequest;
import com.vertex.stockflow.dto.request.RefreshTokenRequest;
import com.vertex.stockflow.dto.response.LoginResponse;
import com.vertex.stockflow.dto.response.RefreshTokenResponse;
import com.vertex.stockflow.dto.response.UserResponse;
import com.vertex.stockflow.entity.RefreshTokenEntity;
import com.vertex.stockflow.security.JwtService;
import com.vertex.stockflow.service.AuthService;
import com.vertex.stockflow.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);

        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(loginRequest.getEmail());
        response.setRefreshtoken(refreshToken.getToken());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshTokenEntity oldToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());// 1. Kiểm tra token cũ xem còn hạn không
        RefreshTokenEntity newToken = refreshTokenService.rotate(oldToken); // 2. Xóa token cũ, tạo Refresh Token mới (xoay vòng)
        UserDetails userDetails = userDetailsService.loadUserByUsername(newToken.getUser().getEmail());// 3. Lấy thông tin User từ DB
        String newAccessToken = jwtService.generateToken(userDetails);
        RefreshTokenResponse response = new RefreshTokenResponse(newAccessToken, newToken.getToken()); // 5. Trả về cặp Token mới cho client
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest request) {
        refreshTokenService.deleteByToken(request.getRefreshToken());
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(Authentication authentication) {
        String email = authentication.getName();
        UserResponse userResponse = authService.getCurrentUser(email);
        return ResponseEntity.ok(userResponse);
    }
}
