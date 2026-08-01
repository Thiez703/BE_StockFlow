package com.vertex.stockflow.dto.response;

import com.vertex.stockflow.common.enums.RoleEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Integer id;
    private String fullName;
    private String email;
    private RoleEnum role;
    private String phone;
}
