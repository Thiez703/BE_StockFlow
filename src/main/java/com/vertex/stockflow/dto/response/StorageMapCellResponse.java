package com.vertex.stockflow.dto.response;

import com.vertex.stockflow.common.enums.LocationStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 1 ô trên sơ đồ = 1 vị trí lưu trữ.
 * Một vị trí có thể chứa nhiều lô/sản phẩm (occupants).
 * Cell trạng thái EMPTY khi occupants rỗng.
 */
@Getter
@Setter
@NoArgsConstructor
public class StorageMapCellResponse {

    // --- Thông tin vị trí ---
    private Integer locationId;
    private String locationCode;      // "A-01"
    private String rowLabel;          // "A"
    private Integer colIndex;         // 1
    private Integer capacity;         // sức chứa tối đa (Thùng), null = không giới hạn
    private Integer usedQuantity;     // tổng quantity tất cả occupant

    // --- Trạng thái tổng hợp: EMPTY nếu không có occupant, ngược lại = worst status ---
    private LocationStatusEnum status;

    // --- Danh sách lô hàng đang chiếm vị trí này ---
    private List<OccupantResponse> occupants;
}
