package com.vertex.stockflow.config;

import com.vertex.stockflow.common.enums.RoleEnum;
import com.vertex.stockflow.entity.UserEntity;
import com.vertex.stockflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@stockflow.com").isEmpty()) {
            UserEntity admin = UserEntity.builder()
                    .fullName("Admin")
                    .email("admin@stockflow.com")
                    .passwordHash(passwordEncoder.encode("Admin@123"))
                    .role(RoleEnum.ADMIN)
                    .isActive(true)
                    .mustChangePassword(false)
                    .build();
            userRepository.save(admin);
            log.info("Default admin account created: admin@stockflow.com / Admin@123");
        }
    }
}
