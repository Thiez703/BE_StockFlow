package com.vertex.stockflow.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
public class TransferDetailResponse {
    private Integer id;
    private Integer productId;
    private String productCode;
    private String productName;
    private String unit;
    private Integer lotId;
    private String lotCode;
    private Integer fromLocationId;
    private String fromLocationCode;
    private Integer toLocationId;
    private String toLocationCode;
    private Integer quantity;
}
