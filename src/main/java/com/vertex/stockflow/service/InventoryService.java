package com.vertex.stockflow.service;

import com.vertex.stockflow.common.enums.RefTypeEnum;
import com.vertex.stockflow.dto.response.InventoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryService {
    void updateInventory(Integer warehouseId, Integer productId, Integer lotId, Integer locationId,
                         Integer quantityDelta, RefTypeEnum refType, Integer refId, Integer createdByUserId);

    Page<InventoryResponse> search(Integer productId, Integer lotId, Integer locationId, Pageable pageable);
}
