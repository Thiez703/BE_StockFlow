package com.vertex.stockflow.dto.request;

import com.vertex.stockflow.common.enums.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//Tạo tk
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    @NotBlank  //kiểm tra tính hợp lệ của dữ liệu đầu vào
    private String fullName;

    @NotBlank
    @Email
    private String email;

    @NotNull
    private RoleEnum role;

    private String phone;
}
