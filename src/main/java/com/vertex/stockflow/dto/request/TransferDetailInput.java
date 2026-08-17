package com.vertex.stockflow.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferDetailInput {

    @NotNull
    private Integer productId;

    @NotNull
    private Integer lotId;

    @NotNull
    private Integer fromLocationId;

    @NotNull
    private Integer toLocationId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
