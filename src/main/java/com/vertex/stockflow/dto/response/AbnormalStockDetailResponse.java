package com.vertex.stockflow.dto.response;

import com.vertex.stockflow.common.enums.AbnormalReasonEnum;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AbnormalStockDetailResponse {
    private Integer id;
    private Integer productId;
    private String productCode;
    private String productName;
    private Integer lotId;
    private String lotCode;
    private Integer locationId;
    private String locationCode;
    private Integer quantity;
    private AbnormalReasonEnum reasonType;
    private String note;
}
