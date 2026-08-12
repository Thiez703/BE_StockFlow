package com.vertex.stockflow.mapper;

import com.vertex.stockflow.dto.response.StocktakeDetailResponse;
import com.vertex.stockflow.dto.response.StocktakeResponse;
import com.vertex.stockflow.entity.StocktakeDetailEntity;
import com.vertex.stockflow.entity.StocktakeEntity;

import java.util.List;

/** Static utility, theo đúng convention của StorageMapMapper trong project. */
public final class StocktakeMapper {

    private StocktakeMapper() {
    }

    public static StocktakeDetailResponse toDetailResponse(StocktakeDetailEntity entity) {
        return StocktakeDetailResponse.builder()
                .id(entity.getId())
                .productId(entity.getProduct().getId())
                .productCode(entity.getProduct().getCode())
                .productName(entity.getProduct().getName())
                .lotId(entity.getLot().getId())
                .lotCode(entity.getLot().getLotCode())
                .locationId(entity.getLocation().getId())
                .locationCode(entity.getLocation().getLocationCode())
                .systemQty(entity.getSystemQty())
                .actualQty(entity.getActualQty())
                .diffQty(entity.getDiffQty())
                .build();
    }

    // Nhận details rời vì StocktakeEntity KHÔNG khai báo @OneToMany ngược tới StocktakeDetailEntity
    // (đúng pattern InboundEntity/OutboundEntity đã có - chứng từ header không giữ list con).
    public static StocktakeResponse toResponse(StocktakeEntity entity, List<StocktakeDetailEntity> details) {
        return StocktakeResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .warehouseId(entity.getWarehouse().getId())
                .warehouseCode(entity.getWarehouse().getCode())
                .createdBy(entity.getCreatedBy() != null ? entity.getCreatedBy().getFullName() : null)
                .approvedBy(entity.getApprovedBy() != null ? entity.getApprovedBy().getFullName() : null)
                .status(entity.getStatus())
                .rejectReason(entity.getRejectReason())
                .note(entity.getNote())
                .createdAt(entity.getCreatedAt())
                .approvedAt(entity.getApprovedAt())
                .details(details.stream().map(StocktakeMapper::toDetailResponse).toList())
                .build();
    }
}
