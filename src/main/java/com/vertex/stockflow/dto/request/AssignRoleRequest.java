package com.vertex.stockflow.dto.request;

import com.vertex.stockflow.common.enums.RoleEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//Phân quyền
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignRoleRequest {
    @NotNull
    private RoleEnum role;

}
