package com.vertex.stockflow.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellThroughRiskResponse {
    private Integer lotId;
    private String lotCode;
    private Integer productId;
    private String productCode;
    private String productName;
    private Integer currentStock;
    private BigDecimal velocity30d;
    private BigDecimal estimatedDaysToSellOut;
    private LocalDate expDate;
    private Integer daysUntilExpiry;
    private boolean atRisk;
}
