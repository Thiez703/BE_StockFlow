package com.vertex.stockflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * CẤU HÌNH BẢO MẬT tạm thời cho giai đoạn phát triển.
 * Dự án có spring-boot-starter-security nên mặc định KHÓA mọi API (401).
 * File này mở toàn bộ endpoint để cả nhóm test CRUD được ngay.
 * TODO: khi làm đăng nhập/JWT thì thay permitAll bằng phân quyền theo RoleEnum.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll());
        return http.build();
    }
}