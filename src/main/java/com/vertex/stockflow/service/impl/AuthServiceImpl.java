package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.AuditAction;
import com.vertex.stockflow.dto.request.ChangePasswordRequest;
import com.vertex.stockflow.dto.request.LoginRequest;
import com.vertex.stockflow.dto.response.LoginResponse;
import com.vertex.stockflow.dto.response.UserResponse;
import com.vertex.stockflow.entity.UserEntity;
import com.vertex.stockflow.exception.IllegalOperationException;
import com.vertex.stockflow.exception.ResourceNotFoundException;
import com.vertex.stockflow.repository.UserRepository;
import com.vertex.stockflow.security.JwtService;
import com.vertex.stockflow.service.AuditLogService;
import com.vertex.stockflow.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    @Override
    public LoginResponse login(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword())); // Lấy thông tin được gửi từ request
        UserDetails userDetails = (UserDetails) authentication.getPrincipal(); // Ép kiểu sang UserDetails để Spring có thể sử dụng và tạo token
        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(token,null);
    }

    @Override
    public UserResponse getCurrentUser(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .phone(user.getPhone())
                .mustChangePassword(user.getMustChangePassword())
                .build();
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request, User actor) {
        UserEntity user = userRepository.findByEmail(actor.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Mật khẩu cũ không đúng");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new IllegalOperationException("Mật khẩu mới không được trùng mật khẩu cũ");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setMustChangePassword(false);
        userRepository.save(user);

        auditLogService.log(actor, AuditAction.CHANGE_PASSWORD, "users", user.getId(),
                "Đổi mật khẩu cho " + user.getEmail());
    }
}
