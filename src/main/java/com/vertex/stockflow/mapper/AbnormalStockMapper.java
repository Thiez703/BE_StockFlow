package com.vertex.stockflow.mapper;

import com.vertex.stockflow.dto.response.AbnormalStockDetailResponse;
import com.vertex.stockflow.dto.response.AbnormalStockResponse;
import com.vertex.stockflow.entity.AbnormalStockDetailEntity;
import com.vertex.stockflow.entity.AbnormalStockEntity;

import java.util.List;

/** Static utility, theo đúng convention của StorageMapMapper/StocktakeMapper trong project. */
public final class AbnormalStockMapper {

    private AbnormalStockMapper() {
    }

    public static AbnormalStockDetailResponse toDetailResponse(AbnormalStockDetailEntity entity) {
        return AbnormalStockDetailResponse.builder()
                .id(entity.getId())
                .productId(entity.getProduct().getId())
                .productCode(entity.getProduct().getCode())
                .productName(entity.getProduct().getName())
                .lotId(entity.getLot().getId())
                .lotCode(entity.getLot().getLotCode())
                .locationId(entity.getLocation().getId())
                .locationCode(entity.getLocation().getLocationCode())
                .quantity(entity.getQuantity())
                .reasonType(entity.getReasonType())
                .note(entity.getNote())
                .build();
    }

    public static AbnormalStockResponse toResponse(AbnormalStockEntity entity, List<AbnormalStockDetailEntity> details) {
        return AbnormalStockResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .warehouseId(entity.getWarehouse().getId())
                .warehouseCode(entity.getWarehouse().getCode())
                .createdBy(entity.getCreatedBy() != null ? entity.getCreatedBy().getEmail() : null)
                .approvedBy(entity.getApprovedBy() != null ? entity.getApprovedBy().getEmail() : null)
                .status(entity.getStatus())
                .rejectReason(entity.getRejectReason())
                .createdAt(entity.getCreatedAt())
                .approvedAt(entity.getApprovedAt())
                .details(details.stream().map(AbnormalStockMapper::toDetailResponse).toList())
                .build();
    }
}
