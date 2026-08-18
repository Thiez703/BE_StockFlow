package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.SupplierRequest;
import com.vertex.stockflow.dto.response.SupplierResponse;

import java.util.List;

public interface SupplierService {
    // Tạo nhà cung cấp mới.
    SupplierResponse create(SupplierRequest request);

    // Cập nhật thông tin nhà cung cấp.
    SupplierResponse update(Integer id, SupplierRequest request);

    // Lấy thông tin nhà cung cấp theo ID.
    SupplierResponse getById(Integer id);

    // Lấy danh sách tất cả nhà cung cấp.
    List<SupplierResponse> getAll();

    // Xóa nhà cung cấp.
    void delete(Integer id);

    // Vô hiệu hóa nhà cung cấp.
    SupplierResponse deactivate(Integer id);

    // Kích hoạt nhà cung cấp.
    SupplierResponse activate(Integer id);
}
