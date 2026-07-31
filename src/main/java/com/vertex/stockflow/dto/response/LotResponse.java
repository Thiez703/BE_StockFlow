package com.vertex.stockflow.dto.response;

import com.vertex.stockflow.common.enums.StatusEnum;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LotResponse {
    private Integer id;
    private Integer productId;
    private String productName;
    private String lotCode;
    private LocalDate mfgDate;
    private LocalDate expDate;
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
