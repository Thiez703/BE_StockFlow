package com.vertex.stockflow.dto.response;

import com.vertex.stockflow.common.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserManagementResponse {
    private Integer id;
    private String fullName;
    private String email;
    private RoleEnum role;
    private  String phone;
    private boolean active;
    private boolean mustChangePassword;
    private LocalDateTime createdAt;
}
