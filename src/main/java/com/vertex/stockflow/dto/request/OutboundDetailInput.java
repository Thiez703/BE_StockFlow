package com.vertex.stockflow.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OutboundDetailInput {

    @NotNull
    private Integer lotId;

    @NotNull
    private Integer locationId;

    @NotNull
    private Integer quantity;

    private String overrideReason;

    private BigDecimal unitPrice;
}