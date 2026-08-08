package com.vertex.stockflow.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class OutboundDetailResponse {
    private Integer id;
    private Integer productId;
    private String productName;
    private Integer lotId;
    private String lotCode;
    private Integer locationId;
    private String locationCode;
    private Integer quantity;
    private String overrideReason;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
}