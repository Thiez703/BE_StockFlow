package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.LotCreateRequest;
import com.vertex.stockflow.dto.request.LotUpdateRequest;
import com.vertex.stockflow.dto.request.SupplierRequest;
import com.vertex.stockflow.dto.response.LotResponse;
import com.vertex.stockflow.dto.response.SupplierResponse;

import java.util.List;

public interface LotService {
    // Tạo lô hàng mới.
    LotResponse create(LotCreateRequest request);

    // Cập nhật thông tin lô hàng.
    LotResponse update(Integer id, LotUpdateRequest request);

    // Lấy thông tin lô hàng theo ID.
    LotResponse getById(Integer id);

    // Lấy danh sách lô hàng theo ID sản phẩm.
    List<LotResponse> getByProductId(Integer productId);

    // Lấy tất cả các lô hàng.
    List<LotResponse> getAll();

    // Xóa lô hàng.
    void delete(Integer id);
}
