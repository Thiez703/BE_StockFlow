package com.vertex.stockflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseUpdateRequest {
    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 255)
    private String address;
}
