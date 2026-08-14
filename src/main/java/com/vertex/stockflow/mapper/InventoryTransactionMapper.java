package com.vertex.stockflow.mapper;

import com.vertex.stockflow.dto.response.InventoryTransactionResponse;
import com.vertex.stockflow.entity.InventoryEntity;
import com.vertex.stockflow.entity.InventoryTransactionEntity;

public final class InventoryTransactionMapper {

    private InventoryTransactionMapper() {
    }

    public static InventoryTransactionResponse toResponse(InventoryTransactionEntity entity, String refCode) {
        InventoryEntity inventory = entity.getInventory();
        return InventoryTransactionResponse.builder()
                .id(entity.getId())
                .refType(entity.getRefType())
                .refId(entity.getRefId())
                .refCode(refCode)
                .productId(inventory.getProduct().getId())
                .productCode(inventory.getProduct().getCode())
                .productName(inventory.getProduct().getName())
                .lotId(inventory.getLot().getId())
                .lotCode(inventory.getLot().getLotCode())
                .locationId(inventory.getLocation() != null ? inventory.getLocation().getId() : null)
                .locationCode(inventory.getLocation() != null ? inventory.getLocation().getLocationCode() : null)
                .quantityChange(entity.getQuantityChange())
                .balanceAfter(entity.getBalanceAfter())
                .performedBy(entity.getCreatedBy().getFullName())
                .performedAt(entity.getCreatedAt())
                .build();
    }
}
