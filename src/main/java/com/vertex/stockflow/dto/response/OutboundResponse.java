package com.vertex.stockflow.dto.response;

import com.vertex.stockflow.common.enums.DocumentStatusEnum;
import com.vertex.stockflow.common.enums.IssueTypeEnum;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class OutboundResponse {
    private Integer id;
    private String code;
    private IssueTypeEnum issueType;
    private Integer customerId;
    private String customerName;
    private Integer warehouseId;
    private DocumentStatusEnum status;
    private String voidReason;
    private String note;
    private LocalDateTime createdAt;
    private List<OutboundDetailResponse> details;
}