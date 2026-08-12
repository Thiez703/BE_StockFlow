package com.vertex.stockflow.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySummaryResponse {
    private Integer productId;
    private String productCode;
    private String productName;
    private String unit;
    private Integer lotId;
    private String lotCode;
    private BigDecimal unitPrice;
    private Integer openingQty;
    private BigDecimal openingValue;
    private Integer inboundQty;
    private BigDecimal inboundValue;
    private Integer outboundQty;
    private BigDecimal outboundValue;
    private Integer closingQty;
    private BigDecimal closingValue;
}
