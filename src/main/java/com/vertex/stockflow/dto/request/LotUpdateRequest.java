package com.vertex.stockflow.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LotUpdateRequest {

    private LocalDate mfgDate;

    @NotNull
    private LocalDate expDate;
}
