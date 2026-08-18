package com.vertex.stockflow.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StocktakeDetailInput {

    @NotNull
    private Integer lotId;

    @NotNull
    private Integer locationId;

    private Integer actualQty;

    @Min(0)
    @NotNull
    private Integer damagedQty = 0;

    @Size(max = 500)
    private String note;
}
