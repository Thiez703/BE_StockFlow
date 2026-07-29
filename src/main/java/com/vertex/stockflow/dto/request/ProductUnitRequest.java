package com.vertex.stockflow.dto.request;

import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUnitRequest {
    @NonNull
    private Integer unitId;
    @NonNull
    @Min(value = 1, message = "Tỷ lệ quy đổi phải lớn hơn 0")
    private Integer conversionRate;

}
