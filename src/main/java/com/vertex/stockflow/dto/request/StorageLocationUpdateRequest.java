package com.vertex.stockflow.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorageLocationUpdateRequest {

    // Không có warehouseId: không cho chuyển 1 vị trí sang kho khác.
    // Vị trí là chỗ vật lý trong nhà kho, nó không "đi" đâu được.

    @NotBlank
    @Size(max = 5)
    private String rowLabel;

    @NotNull
    @Min(1)
    @Max(6)
    private Integer colIndex;

    // Update giờ nghĩa là "dời vị trí này sang ô khác trên lưới".
    // locationCode được sinh lại theo toạ độ mới.
}