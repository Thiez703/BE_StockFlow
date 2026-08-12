package com.vertex.stockflow.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LowStockAlertResponse {
    private Integer productId;
    private String productCode;
    private String productName;
    private String unit;
    private Integer currentStock;
    private Integer threshold;
    private String thresholdSource;
    private BigDecimal velocity30d;
    private BigDecimal estimatedDaysRemaining;
}
