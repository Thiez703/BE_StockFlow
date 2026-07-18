package com.vertex.stockflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.vertex.stockflow.repository.UserRepository;

@SpringBootApplication
public class BeStockflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeStockflowApplication.class, args);
    }
    //Ai viết
    @Bean
    public CommandLineRunner updatePassword(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            userRepository.findByUsername("admin_test").ifPresent(user -> {
                user.setPasswordHash(passwordEncoder.encode("123456"));
                userRepository.save(user);
                System.out.println("====== DÃ RESET MẬT KHẨU ADMIN_TEST THÀNH 123456 ======");
            });
        };
    }
}
