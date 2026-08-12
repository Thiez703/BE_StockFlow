package com.vertex.stockflow.dto.response;

import com.vertex.stockflow.common.enums.RefTypeEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class InventoryTransactionResponse {
    private Long id;
    private RefTypeEnum refType;
    private Integer refId;
    private String refCode;
    private Integer productId;
    private String productCode;
    private String productName;
    private Integer lotId;
    private String lotCode;
    private Integer locationId;
    private String locationCode;
    private Integer quantityChange;
    private Integer balanceAfter;
    private String performedBy;
    private LocalDateTime performedAt;
}
