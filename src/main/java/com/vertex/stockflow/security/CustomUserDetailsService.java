package com.vertex.stockflow.security;

import com.vertex.stockflow.entity.UserEntity;
import com.vertex.stockflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

        private final UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                UserEntity userEntity = userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + username));
                return User.builder()
                        .username(userEntity.getEmail())
                        .password(userEntity.getPasswordHash())
                        .disabled(!Boolean.TRUE.equals(userEntity.getIsActive()))
                        .authorities(new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().name()))
                        .build();
        }
}
