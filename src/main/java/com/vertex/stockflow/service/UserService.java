package com.vertex.stockflow.service;

import com.vertex.stockflow.common.enums.RoleEnum;
import com.vertex.stockflow.dto.request.AssignRoleRequest;
import com.vertex.stockflow.dto.request.CreateUserRequest;
import com.vertex.stockflow.dto.request.UpdateUserRequest;
import com.vertex.stockflow.dto.response.UserManagementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;

public interface UserService {
    UserManagementResponse create(CreateUserRequest request, User actor);
    UserManagementResponse update(Integer id, UpdateUserRequest request, User actor);
    void lock(Integer id, User actor);
    void unlock(Integer id, User actor);
    UserManagementResponse assignRole(Integer id, AssignRoleRequest request, User actor);
    void resetPassword(Integer id, User actor);
    Page<UserManagementResponse> search(String keyword, RoleEnum role, Boolean isActive, Pageable pageable);
    UserManagementResponse getById(Integer id);
}
