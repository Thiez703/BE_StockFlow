package com.vertex.stockflow.dto.request;

import com.vertex.stockflow.common.enums.IssueTypeEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OutboundCreateRequest {

    @NotNull
    private Integer warehouseId;

    @NotNull
    private IssueTypeEnum issueType;

    private Integer customerId;

    @Size(max = 255)
    private String note;

    @NotEmpty
    @Valid
    private List<OutboundDetailInput> details;
}