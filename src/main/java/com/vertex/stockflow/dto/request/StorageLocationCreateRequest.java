package com.vertex.stockflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorageLocationCreateRequest {
    @NotNull
    private Integer warehouseId;

    @Size(max = 20)
    private String zoneCode;

    @NotBlank
    @Size(max = 20)
    private String locationCode;
}
