package com.vertex.stockflow.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUnitResponse {
    private Integer id;
    private Integer unitId;
    private String unitCode;
    private String unitName;
    private Integer conversionRate;
}
