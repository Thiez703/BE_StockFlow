package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.dto.request.LoginRequest;
import com.vertex.stockflow.dto.response.LoginResponse;
import com.vertex.stockflow.dto.response.UserResponse;
import com.vertex.stockflow.entity.UserEntity;
import com.vertex.stockflow.repository.UserRepository;
import com.vertex.stockflow.security.JwtService;
import com.vertex.stockflow.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Override
    public LoginResponse login(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword())); //Lấy thông tin được gửi từ req
        UserDetails userDetails = (UserDetails) authentication.getPrincipal(); //ép kiểu sang UserDetail để Spring có thể sử dụng nó và tạo ra token
        String token = jwtService.generateToken(userDetails);

        UserEntity userEntity = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponse userResponse = new UserResponse(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getRole().name()
        );

        return new LoginResponse(token,null, userResponse);
    }
}
