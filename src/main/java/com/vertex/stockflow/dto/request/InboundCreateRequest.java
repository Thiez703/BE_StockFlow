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
public class InboundCreateRequest {

    @NotNull
    private Integer warehouseId;

    @NotNull
    private Integer supplierId;

    @Size(max = 255)
    private String note;

    @NotEmpty
    @Valid
    private List<InboundDetailInput> details;
}
