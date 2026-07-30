package com.vertex.stockflow.mapper;

import com.vertex.stockflow.dto.response.UserManagementResponse;
import com.vertex.stockflow.entity.UserEntity;
import org.springframework.stereotype.Component;

//"dịch" dữ liệu từ UserEntity sang UserManagementResponse -> bảo mật
@Component
public class UserMapper {
    public UserManagementResponse toResponse(UserEntity user) {
        UserManagementResponse response = new UserManagementResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setPhone(user.getPhone());
        response.setActive(user.getIsActive());
        response.setMustChangePassword(user.getMustChangePassword());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
