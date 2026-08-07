package com.vertex.stockflow.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StocktakeCreateRequest {

    @NotNull
    private Integer warehouseId;

    @Size(max = 255)
    private String note;

    // Client tự gửi actualQty đã đếm cho từng dòng (sau khi đã tham khảo khung tồn kho
    // từ GET /api/stocktakes/inventory-snapshot) - server tự tính lại systemQty/diffQty,
    // không tin số systemQty nào từ client.
    @NotEmpty
    @Valid
    private List<StocktakeDetailInput> details;
}
