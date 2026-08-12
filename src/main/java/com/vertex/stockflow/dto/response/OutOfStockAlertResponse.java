package com.vertex.stockflow.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutOfStockAlertResponse {
    private Integer productId;
    private String productCode;
    private String productName;
    private String unit;
    private BigDecimal velocity30d;
}
