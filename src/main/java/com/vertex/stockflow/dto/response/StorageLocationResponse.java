package com.vertex.stockflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorageLocationResponse {
    private Integer id;
    private Integer warehouseId;
    private String zoneCode;
    private String locationCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
