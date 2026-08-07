package com.vertex.stockflow.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StocktakeDetailResponse {
    private Integer id;
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
}
