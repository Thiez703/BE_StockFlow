package com.vertex.stockflow.dto.response;

import com.vertex.stockflow.common.enums.LocationStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 1 ô trên sơ đồ. Cố ý để PHẲNG (không lồng object lot/product bên trong)
 * để FE chỉ cần check status == EMPTY thay vì phải cell.lot?.lotCode khắp nơi.
 */
@Getter
@Setter
@NoArgsConstructor
public class StorageMapCellResponse {

    // --- Thông tin vị trí: LUÔN có, kể cả ô trống ---
    private Integer locationId;
    private String locationCode;      // "A-01"
    private String rowLabel;          // "A"
    private Integer colIndex;         // 1

    // --- Trạng thái: KHÔNG lấy từ DB, được Service tính rồi set vào ---
    private LocationStatusEnum status;

    // --- Thông tin lô: null khi ô trống ---
    private Integer lotId;
    private String lotCode;
    private LocalDate expDate;
    private Integer daysToExpiry;     // cũng do Service tính, không có trong DB

    // --- Thông tin sản phẩm: null khi ô trống ---
    private Integer productId;
    private String productCode;       // "SP-003"
    private String productName;
    private String unit;              // "Lon"
    private Integer quantity;         // tồn kho hiện tại của lô này tại ô này
    private Integer minStock;         // định mức tối thiểu; null -> FE hiện "-"

    /**
     * Constructor riêng cho JPQL constructor expression.
     * KHÔNG có status và daysToExpiry vì 2 field đó không lấy được từ DB.
     * Thứ tự tham số ở đây phải khớp TUYỆT ĐỐI với thứ tự cột trong câu SELECT new (...).
     */
    public StorageMapCellResponse(Integer locationId, String locationCode, String rowLabel, Integer colIndex,
                                  Integer lotId, String lotCode, LocalDate expDate,
                                  Integer productId, String productCode, String productName,
                                  String unit, Integer quantity, Integer minStock) {
        this.locationId = locationId;
        this.locationCode = locationCode;
        this.rowLabel = rowLabel;
        this.colIndex = colIndex;
        this.lotId = lotId;
        this.lotCode = lotCode;
        this.expDate = expDate;
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.unit = unit;
        this.quantity = quantity;
        this.minStock = minStock;
    }
}