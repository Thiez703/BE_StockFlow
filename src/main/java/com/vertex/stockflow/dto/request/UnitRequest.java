package com.vertex.stockflow.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnitRequest {
    @NotNull
    @Size(min = 1, max = 10, message = "Mã phải có từ 1 đến 10 ký tự")
    private String code;
    @NotNull
    @Size(min = 1, max = 20, message = "Tên phải có từ 1 đến 20 ký tự")
    private String name;
}
