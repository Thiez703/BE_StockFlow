package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.AuditAction;
import com.vertex.stockflow.common.util.PasswordGenerator;
import com.vertex.stockflow.dto.request.AssignRoleRequest;
import com.vertex.stockflow.dto.request.CreateUserRequest;
import com.vertex.stockflow.dto.request.UpdateUserRequest;
import com.vertex.stockflow.dto.response.UserManagementResponse;
import com.vertex.stockflow.entity.UserEntity;
import com.vertex.stockflow.exception.DuplicateResourceException;
import com.vertex.stockflow.exception.ResourceNotFoundException;
import com.vertex.stockflow.mapper.UserMapper;
import com.vertex.stockflow.repository.UserRepository;
import com.vertex.stockflow.service.AuditLogService;
import com.vertex.stockflow.service.MailService;
import com.vertex.stockflow.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    // Lombok sinh 1 constructor duy nhất nhận đủ 6 field final này -> Spring dùng nó để inject bean (constructor injection)
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final PasswordGenerator passwordGenerator;
    private final MailService mailService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public UserManagementResponse create(CreateUserRequest req, User actor) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + req.getEmail());
        }

        String rawPassword = passwordGenerator.generate();
        UserEntity user = UserEntity.builder()
                .fullName(req.getFullName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .role(req.getRole())
                .passwordHash(passwordEncoder.encode(rawPassword))
                .isActive(true)
                .mustChangePassword(true)
                .build();
        userRepository.save(user);

        mailService.sendGeneratedPassword(user.getEmail(), user.getFullName(), rawPassword);
        auditLogService.log(actor, AuditAction.CREATE_USER, "users", user.getId(),
                "Tạo tài khoản " + user.getEmail());
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserManagementResponse update(Integer id, UpdateUserRequest req, User actor) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setFullName(req.getFullName());
        user.setPhone(req.getPhone());
        userRepository.save(user);

        auditLogService.log(actor, AuditAction.UPDATE_USER, "users", id,
                "Cập nhật thông tin tài khoản " + user.getEmail());
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void lock(Integer id, User actor) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setIsActive(false);
        userRepository.save(user);
        auditLogService.log(actor, AuditAction.LOCK_USER, "users", id, "Khóa tài khoản " + user.getEmail());
    }

    @Override
    @Transactional
    public void unlock(Integer id, User actor) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setIsActive(true);
        userRepository.save(user);
        auditLogService.log(actor, AuditAction.UNLOCK_USER, "users", id, "Mở khóa tài khoản " + user.getEmail());
    }

    @Override
    @Transactional
    public UserManagementResponse assignRole(Integer id, AssignRoleRequest req, User actor) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        String oldRole = user.getRole().name();
        user.setRole(req.getRole());
        userRepository.save(user);

        auditLogService.log(actor, AuditAction.CHANGE_ROLE, "users", id,
                "Đổi vai trò " + user.getEmail() + ": " + oldRole + " -> " + req.getRole());
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void resetPassword(Integer id, User actor) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        String rawPassword = passwordGenerator.generate();
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setMustChangePassword(true);
        userRepository.save(user);

        mailService.sendGeneratedPassword(user.getEmail(), user.getFullName(), rawPassword);
        auditLogService.log(actor, AuditAction.RESET_PASSWORD, "users", id,
                "Đặt lại mật khẩu cho " + user.getEmail());
    }
}
