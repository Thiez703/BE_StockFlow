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

    private Integer categoryId;
    private String unit;

    private Integer minStock;
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
