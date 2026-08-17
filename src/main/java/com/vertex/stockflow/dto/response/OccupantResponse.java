package com.vertex.stockflow.dto.response;

import com.vertex.stockflow.common.enums.LocationStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 1 lô hàng đang nằm trong 1 vị trí.
 * 1 vị trí có thể có nhiều occupant (nhiều lô/sản phẩm khác nhau).
 */
@Getter
@Setter
@NoArgsConstructor
public class OccupantResponse {

    private Integer lotId;
    private String lotCode;
    private LocalDate expDate;
    private Integer daysToExpiry;

    private Integer productId;
    private String productCode;
    private String productName;
    private String unit;
    private Integer quantity;
    private Integer minStock;

    private LocationStatusEnum status;
}
