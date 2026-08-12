package com.vertex.stockflow.mapper;

import com.vertex.stockflow.dto.response.InboundDetailResponse;
import com.vertex.stockflow.dto.response.InboundResponse;
import com.vertex.stockflow.entity.InboundDetailEntity;
import com.vertex.stockflow.entity.InboundEntity;

import java.util.List;

public final class InboundMapper {

    private InboundMapper() {
    }

    public static InboundDetailResponse toDetailResponse(InboundDetailEntity entity) {
        return InboundDetailResponse.builder()
                .id(entity.getId())
                .productId(entity.getProduct().getId())
                .productName(entity.getProduct().getName())
                .lotId(entity.getLot().getId())
                .lotCode(entity.getLot().getLotCode())
                .locationId(entity.getLocation().getId())
                .locationCode(entity.getLocation().getLocationCode())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .totalAmount(entity.getTotalAmount())
                .build();
    }

    public static InboundResponse toResponse(InboundEntity inbound, List<InboundDetailEntity> details) {
        return InboundResponse.builder()
                .id(inbound.getId())
                .code(inbound.getCode())
                .supplierId(inbound.getSupplier().getId())
                .supplierName(inbound.getSupplier().getName())
                .warehouseId(inbound.getWarehouse().getId())
                .status(inbound.getStatus())
                .voidReason(inbound.getVoidReason())
                .note(inbound.getNote())
                .createdAt(inbound.getCreatedAt())
                .details(details.stream().map(InboundMapper::toDetailResponse).toList())
                .build();
    }
}
