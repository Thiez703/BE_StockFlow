package com.vertex.stockflow.dto.request;

import com.vertex.stockflow.common.enums.StatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

// Dùng chung cho cả create và update Product vì 2 thao tác này có cùng tập field đầu vào.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank
    // Khớp với giới hạn length = 30 của cột code trong ProductEntity.
    @Size(max = 30)
    private String code;

    @NotBlank
    // Khớp với giới hạn length = 255 của cột name trong ProductEntity.
    @Size(max = 255)
    private String name;

    // Cho phép null: category_id trong ProductEntity không nullable = false.
    private Integer categoryId;

    // base_unit_id không cho null trong ProductEntity (nullable = false).
    @NotNull
    private Integer baseUnitId;

    // BR-18: min_stock nullable, không ràng buộc thêm.
    private Integer minStock;

    private StatusEnum status;
}
