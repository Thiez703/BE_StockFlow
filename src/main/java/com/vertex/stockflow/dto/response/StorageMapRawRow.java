package com.vertex.stockflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Flat row trả về từ JPQL query cho sơ đồ kho.
 * Mỗi row = 1 cặp (location, inventory). Vị trí trống có lot/product = null.
 * Service sẽ gom nhóm theo locationId thành StorageMapCellResponse + OccupantResponse.
 */
@Getter
@AllArgsConstructor
public class StorageMapRawRow {

    // Location info
    private final Integer locationId;
    private final String locationCode;
    private final String rowLabel;
    private final Integer colIndex;
    private final Integer capacity;

    // Lot info (null khi vị trí trống)
    private final Integer lotId;
    private final String lotCode;
    private final LocalDate expDate;

    // Product info (null khi vị trí trống)
    private final Integer productId;
    private final String productCode;
    private final String productName;
    private final String unit;
    private final Integer quantity;
    private final Integer minStock;
}
