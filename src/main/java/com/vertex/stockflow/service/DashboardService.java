package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.response.StorageMapResponse;

public interface DashboardService {
    // Lấy sơ đồ/bản đồ sức chứa của kho (storage map).
    StorageMapResponse getStorageMap(Integer warehouseId);
}