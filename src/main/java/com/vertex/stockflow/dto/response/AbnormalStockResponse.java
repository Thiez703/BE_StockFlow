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
public class AbnormalStockResponse {
    private Integer id;
    private String code;
    private Integer warehouseId;
    private String warehouseCode;

    private String createdBy;
    private String approvedBy;

    private ApprovalStatusEnum status;
    private String rejectReason;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;

    private List<AbnormalStockDetailResponse> details;
}
