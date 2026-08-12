package com.vertex.stockflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InboundDetailInput {

    @NotNull
    private Integer productId;

    // --- Lô hàng: chọn lô có sẵn HOẶC tạo mới inline ---
    // Nếu lotId != null → dùng lô đã tồn tại.
    // Nếu lotId == null → tạo lô mới từ lotCode + expDate (+ mfgDate tùy chọn).
    private Integer lotId;

    @Size(max = 30)
    private String lotCode;

    private LocalDate mfgDate;

    private LocalDate expDate;

    @NotNull
    private Integer locationId;

    @NotNull
    @Positive
    private Integer quantity;

    @NotNull
    private BigDecimal unitPrice;
}
