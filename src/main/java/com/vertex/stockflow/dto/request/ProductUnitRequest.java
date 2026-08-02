package com.vertex.stockflow.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUnitRequest {
    @NotNull
    private Integer unitId;
    @NotNull
    @Min(value = 1, message = "Tỷ lệ quy đổi phải lớn hơn 0")
    private Integer conversionRate;

}
