package com.vertex.stockflow.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

// productId KHÔNG cần gửi - suy ra từ lot.getProduct() ở tầng service.
// locationId BẮT BUỘC phải gửi rõ - không suy luận ngầm, vì 1 lô có thể nằm ở nhiều vị trí
// (uk_inventory_location chỉ đảm bảo 1 vị trí = 1 lô, không đảm bảo chiều ngược lại).
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StocktakeDetailInput {

    @NotNull
    private Integer lotId;

    @NotNull
    private Integer locationId;

    @NotNull
    private Integer actualQty;
}
