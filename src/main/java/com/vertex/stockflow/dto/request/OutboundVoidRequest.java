package com.vertex.stockflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OutboundVoidRequest {
    @NotBlank
    private String reason;
}