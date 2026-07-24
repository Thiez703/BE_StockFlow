package com.vertex.stockflow.config;

import com.vertex.stockflow.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // tắt khiên chống tấn công mạng kiểu cũ , mục đích cho máy chủ nhẹ hơn
          .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))//Vô trạng thái, không lưu seesion, mục đích làm nhẹ máy chủ
              .authorizeHttpRequests(s -> s
                  .requestMatchers("/api/auth/**").permitAll()
                  .requestMatchers("/error").permitAll()
                  .anyRequest().authenticated())
              .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // thêm bộ lọc jwt vào trước bộ lọc xác thực người dùng
      return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("123456"));
    }

}
