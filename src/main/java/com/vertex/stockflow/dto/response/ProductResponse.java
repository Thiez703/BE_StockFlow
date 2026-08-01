package com.vertex.stockflow.dto.response;

import com.vertex.stockflow.common.enums.StatusEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Integer id;
    private String code;
    private String name;

    // Chỉ trả categoryId/baseUnitId (không trả nguyên entity quan hệ) - cùng convention với CategoryResponse.
    private Integer categoryId;
    private Integer baseUnitId;

    private Integer minStock;
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
