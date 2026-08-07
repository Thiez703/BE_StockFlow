package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.response.StorageMapResponse;

public interface DashboardService {

    StorageMapResponse getStorageMap(Integer warehouseId);
}