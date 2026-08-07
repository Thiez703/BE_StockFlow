package com.vertex.stockflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AbnormalStockRejectRequest {

    @NotBlank
    @Size(max = 255)
    private String rejectReason;
}
