package com.vertex.stockflow.service;

import com.vertex.stockflow.common.enums.RoleEnum;
import com.vertex.stockflow.entity.UserEntity;
import com.vertex.stockflow.repository.UserRepository;
import com.vertex.stockflow.security.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private UserEntity buildUser(boolean isActive) {
        return UserEntity.builder()
                .id(1)
                .email("test@example.com")
                .passwordHash("$2a$10$hashedpassword")
                .role(RoleEnum.STAFF)
                .isActive(isActive)
                .build();
    }

    @Test
    @DisplayName("Active user → UserDetails.isEnabled() = true")
    void activeUser_returnsEnabledUserDetails() {
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(buildUser(true)));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@example.com");

        assertTrue(userDetails.isEnabled());
        assertEquals("test@example.com", userDetails.getUsername());
    }

    @Test
    @DisplayName("Locked user (isActive=false) → UserDetails.isEnabled() = false")
    void lockedUser_returnsDisabledUserDetails() {
        when(userRepository.findByEmail("locked@example.com"))
                .thenReturn(Optional.of(buildUser(false)));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("locked@example.com");

        assertFalse(userDetails.isEnabled(), "User bị lock phải có isEnabled()=false");
    }

    @Test
    @DisplayName("Unknown email → UsernameNotFoundException")
    void unknownEmail_throwsUsernameNotFoundException() {
        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("unknown@example.com"));
    }
}
