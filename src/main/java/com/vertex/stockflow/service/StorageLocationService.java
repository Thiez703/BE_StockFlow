package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.StorageLocationCreateRequest;
import com.vertex.stockflow.dto.request.StorageLocationUpdateRequest;
import com.vertex.stockflow.dto.response.StorageLocationResponse;
import com.vertex.stockflow.entity.WarehouseEntity;

import java.util.List;

public interface StorageLocationService {
    // Tạo vị trí lưu trữ mới.
    StorageLocationResponse create(StorageLocationCreateRequest request);

    // Cập nhật thông tin vị trí lưu trữ.
    StorageLocationResponse update(Integer id, StorageLocationUpdateRequest request);

    // Xóa vị trí lưu trữ.
    void delete(Integer id);

    // Lấy thông tin vị trí lưu trữ theo ID.
    StorageLocationResponse getById(Integer id);

    // Lấy danh sách vị trí lưu trữ theo kho.
    List<StorageLocationResponse> getByWarehouseId(Integer warehouseId);

    // Lấy tất cả các vị trí lưu trữ.
    List<StorageLocationResponse> getAll();

    // Vô hiệu hóa vị trí lưu trữ.
    StorageLocationResponse deactivate(Integer id);

    // Kích hoạt vị trí lưu trữ.
    StorageLocationResponse activate(Integer id);

    // Sinh tự động các vị trí lưu trữ mặc định khi tạo kho.
    void createDefaultLocations(WarehouseEntity warehouse);
}
