package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.WarehouseCreateRequest;
import com.vertex.stockflow.dto.request.WarehouseUpdateRequest;
import com.vertex.stockflow.dto.response.WarehouseResponse;

import java.util.List;

public interface WarehouseService {
    // Tạo kho lưu trữ mới.
    WarehouseResponse create(WarehouseCreateRequest request);

    // Cập nhật thông tin kho lưu trữ.
    WarehouseResponse update(Integer id, WarehouseUpdateRequest request);

    // Xóa kho lưu trữ.
    void delete(Integer id);

    // Lấy thông tin kho lưu trữ theo ID.
    WarehouseResponse getById(Integer id);

    // Lấy danh sách tất cả các kho lưu trữ.
    List<WarehouseResponse> getAll();
}
