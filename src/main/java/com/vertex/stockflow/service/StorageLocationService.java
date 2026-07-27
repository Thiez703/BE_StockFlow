package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.StorageLocationCreateRequest;
import com.vertex.stockflow.dto.request.StorageLocationUpdateRequest;
import com.vertex.stockflow.dto.response.StorageLocationResponse;
import com.vertex.stockflow.entity.WarehouseEntity;

import java.util.List;

public interface StorageLocationService {
    StorageLocationResponse create(StorageLocationCreateRequest request);
    StorageLocationResponse update(Integer id, StorageLocationUpdateRequest request);
    void delete(Integer id);
    StorageLocationResponse getById(Integer id);
    List<StorageLocationResponse> getByWarehouseId(Integer warehouseId);
    List<StorageLocationResponse> getAll();

    // Được WarehouseServiceImpl gọi trong cùng transaction khi tạo kho mới (BR-17)
    void createDefaultLocation(WarehouseEntity warehouse);
}
