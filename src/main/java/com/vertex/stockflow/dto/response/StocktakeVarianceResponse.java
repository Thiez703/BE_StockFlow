package com.vertex.stockflow.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StocktakeVarianceResponse {
    private Integer stocktakeId;
    private String stocktakeCode;
    private Integer warehouseId;
    private String warehouseCode;
    private LocalDateTime approvedAt;
    private Integer productId;
    private String productCode;
    private String productName;
    private Integer lotId;
    private String lotCode;
    private Integer locationId;
    private String locationCode;
    private Integer systemQty;
    private Integer actualQty;
    private Integer diffQty;
    private String note;
}
