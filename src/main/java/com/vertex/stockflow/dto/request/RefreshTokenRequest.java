package com.vertex.stockflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class RefreshTokenRequest {
    @NotBlank
    private String refreshToken;

}
