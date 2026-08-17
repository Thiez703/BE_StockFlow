package com.vertex.stockflow.dto.response;

import com.vertex.stockflow.common.enums.DocumentStatusEnum;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class TransferResponse {
    private Integer id;
    private String code;
    private Integer warehouseId;
    private DocumentStatusEnum status;
    private String voidReason;
    private String note;
    private String createdByName;
    private LocalDateTime createdAt;
    private List<TransferDetailResponse> details;
}
