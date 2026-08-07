package com.vertex.stockflow.service;

import com.vertex.stockflow.common.enums.RefTypeEnum;

public interface InventoryService {
    void updateInventory(Integer warehouseId, Integer productId, Integer lotId, Integer locationId,
                         Integer quantityDelta, RefTypeEnum refType, Integer refId, Integer createdByUserId);
}
