package com.vertex.stockflow.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class InventoryResponse {
    private Integer id;
    private Integer warehouseId;
    private String warehouseCode;
    private Integer productId;
    private String productCode;
    private String productName;
    private Integer lotId;
    private String lotCode;
    private Integer locationId;
    private String locationCode;
    private Integer quantity;
    private LocalDateTime updatedAt;
}
