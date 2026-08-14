package com.vertex.stockflow.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryByProductResponse {
    private Integer productId;
    private String productCode;
    private String productName;
    private Long totalQuantity;
}
