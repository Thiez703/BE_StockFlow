package com.vertex.stockflow.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AbnormalStockCreateRequest {

    @NotNull
    private Integer warehouseId;

    @NotEmpty
    @Valid
    private List<AbnormalStockDetailInput> details;
}
