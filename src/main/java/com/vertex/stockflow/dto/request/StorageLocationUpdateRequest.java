package com.vertex.stockflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorageLocationUpdateRequest {
    @Size(max = 20)
    private String zoneCode;

    @NotBlank
    @Size(max = 20)
    private String locationCode;
}
