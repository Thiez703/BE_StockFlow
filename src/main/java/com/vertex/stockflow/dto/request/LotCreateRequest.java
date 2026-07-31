package com.vertex.stockflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LotCreateRequest {

    @NotNull
    private Integer productId;

    @NotBlank
    @Size(max = 30)
    private String lotCode;

    private LocalDate mfgDate;

    @NotNull
    private LocalDate expDate;
}
