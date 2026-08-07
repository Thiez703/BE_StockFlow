package com.vertex.stockflow.dto.response;

import com.vertex.stockflow.common.enums.ApprovalStatusEnum;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StocktakeResponse {
    private Integer id;
    private String code;
    private Integer warehouseId;
    private String warehouseCode;

    // Chỉ trả email người tạo/duyệt (không trả nguyên UserEntity) - cùng quy ước với CategoryResponse.
    private String createdBy;
    private String approvedBy;

    private ApprovalStatusEnum status;
    private String rejectReason;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;

    private List<StocktakeDetailResponse> details;
}
