package com.vertex.stockflow.mapper;

import com.vertex.stockflow.dto.response.UserManagementResponse;
import com.vertex.stockflow.entity.UserEntity;
import org.springframework.stereotype.Component;
//"dịch" dữ liệu từ UserEntity sang UserManagementResponse -> bảo mật
@Component
public class UserMapper {
    public UserManagementResponse toResponse(UserEntity user) {
        UserManagementResponse res = new UserManagementResponse();
        res.setId(user.getId());
        res.setFullName(user.getFullName());
        res.setEmail(user.getEmail());
        res.setRole(user.getRole());
        res.setPhone(user.getPhone());
        res.setActive(user.getIsActive());
        res.setMustChangePassword(user.getMustChangePassword());
        res.setCreatedAt(user.getCreatedAt());
        return res;
    }
}
