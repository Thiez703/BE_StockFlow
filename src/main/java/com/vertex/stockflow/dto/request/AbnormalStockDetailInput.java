package com.vertex.stockflow.dto.request;

import com.vertex.stockflow.common.enums.AbnormalReasonEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

// productId KHÔNG cần gửi - suy ra từ lot.getProduct(). locationId BẮT BUỘC gửi rõ,
// cùng lý do với StocktakeDetailInput (1 lô có thể nằm ở nhiều vị trí).
// note validate "bắt buộc khi reasonType = OTHER" nằm ở tầng service, không phải annotation ở đây
// (project chưa có tiền lệ cross-field validator, không tự chế thêm cho riêng DTO này).
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AbnormalStockDetailInput {

    @NotNull
    private Integer lotId;

    @NotNull
    private Integer locationId;

    @NotNull
    @Positive
    private Integer quantity;

    @NotNull
    private AbnormalReasonEnum reasonType;

    @Size(max = 255)
    private String note;
}
