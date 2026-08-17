package com.vertex.stockflow.mapper;

import com.vertex.stockflow.dto.response.OutboundDetailResponse;
import com.vertex.stockflow.dto.response.OutboundResponse;
import com.vertex.stockflow.entity.OutboundDetailEntity;
import com.vertex.stockflow.entity.OutboundEntity;

import java.util.List;

public final class OutboundMapper {

    private OutboundMapper() {
    }

    public static OutboundDetailResponse toDetailResponse(OutboundDetailEntity entity) {
        return OutboundDetailResponse.builder()
                .id(entity.getId())
                .productId(entity.getProduct().getId())
                .productName(entity.getProduct().getName())
                .lotId(entity.getLot().getId())
                .lotCode(entity.getLot().getLotCode())
                .locationId(entity.getLocation().getId())
                .locationCode(entity.getLocation().getLocationCode())
                .quantity(entity.getQuantity())
                .overrideReason(entity.getOverrideReason())
                .unitPrice(entity.getUnitPrice())
                .totalAmount(entity.getTotalAmount())
                .build();
    }

    public static OutboundResponse toResponse(OutboundEntity outbound, List<OutboundDetailEntity> details) {
        return OutboundResponse.builder()
                .id(outbound.getId())
                .code(outbound.getCode())
                .issueType(outbound.getIssueType())
                .customerId(outbound.getCustomer() != null ? outbound.getCustomer().getId() : null)
                .customerName(outbound.getCustomer() != null ? outbound.getCustomer().getName() : null)
                .warehouseId(outbound.getWarehouse().getId())
                .createdByName(outbound.getCreatedBy() != null ? outbound.getCreatedBy().getFullName() : null)
                .status(outbound.getStatus())
                .voidReason(outbound.getVoidReason())
                .note(outbound.getNote())
                .createdAt(outbound.getCreatedAt())
                .details(details.stream().map(OutboundMapper::toDetailResponse).toList())
                .build();
    }
}