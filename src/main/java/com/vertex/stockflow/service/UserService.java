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
    // Tạo người dùng mới.
    UserManagementResponse create(CreateUserRequest request, User actor);

    // Cập nhật thông tin người dùng.
    UserManagementResponse update(Integer id, UpdateUserRequest request, User actor);

    // Khóa tài khoản người dùng.
    void lock(Integer id, User actor);

    // Mở khóa tài khoản người dùng.
    void unlock(Integer id, User actor);

    // Phân quyền cho người dùng.
    UserManagementResponse assignRole(Integer id, AssignRoleRequest request, User actor);

    // Đặt lại mật khẩu cho người dùng.
    void resetPassword(Integer id, User actor);

    // Tìm kiếm người dùng theo các tiêu chí.
    Page<UserManagementResponse> search(String keyword, RoleEnum role, Boolean isActive, Pageable pageable);

    // Lấy thông tin người dùng theo ID.
    UserManagementResponse getById(Integer id);
}
