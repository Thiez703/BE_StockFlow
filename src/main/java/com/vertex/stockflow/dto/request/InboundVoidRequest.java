package com.vertex.stockflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InboundVoidRequest {
    @NotBlank
    private String reason;
}
