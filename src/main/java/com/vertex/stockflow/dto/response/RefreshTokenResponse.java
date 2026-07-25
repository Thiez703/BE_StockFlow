package com.vertex.stockflow.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
}
