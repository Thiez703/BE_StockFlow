package com.vertex.stockflow.dto.response;

import com.vertex.stockflow.common.enums.StatusEnum;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DỮ LIỆU TRẢ VỀ cho FE sau mỗi thao tác (create/get/update).
 * Quan hệ category/baseUnit được "làm phẳng" thành id + name để JSON đơn giản,
 * tránh trả cả cục Entity lồng nhau.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Integer id;
    private String barcode;
    private String name;

    private Integer categoryId;     // có thể null nếu chưa gán danh mục
    private String categoryName;

    private Integer baseUnitId;
    private String baseUnitName;

    private Integer minStock;
    private Integer maxStock;

    private StatusEnum status;      // ACTIVE hoặc INACTIVE (đã xóa mềm)

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}