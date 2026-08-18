package com.vertex.stockflow.service;

import com.vertex.stockflow.common.enums.RefTypeEnum;
import com.vertex.stockflow.dto.response.InventoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryService {
    // Cập nhật số lượng tồn kho.
    void updateInventory(Integer warehouseId, Integer productId, Integer lotId, Integer locationId,
                         Integer quantityDelta, RefTypeEnum refType, Integer refId, Integer createdByUserId);

    // Tìm kiếm chi tiết tồn kho (theo sản phẩm, lô, vị trí).
    Page<InventoryResponse> search(Integer productId, Integer lotId, Integer locationId, Pageable pageable);

    // Tìm kiếm tổng hợp tồn kho theo sản phẩm.
    Page<com.vertex.stockflow.dto.response.InventoryByProductResponse> searchByProduct(Integer productId, Pageable pageable);
}
