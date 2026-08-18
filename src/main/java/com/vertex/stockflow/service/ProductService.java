package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.ProductRequest;
import com.vertex.stockflow.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    // Tạo sản phẩm mới.
    ProductResponse create(ProductRequest request);

    // Cập nhật thông tin sản phẩm.
    ProductResponse update(Integer id, ProductRequest request);

    // Xóa sản phẩm.
    void delete(Integer id);

    // Vô hiệu hóa sản phẩm.
    ProductResponse deactivate(Integer id);

    // Kích hoạt sản phẩm.
    ProductResponse activate(Integer id);

    // Lấy thông tin sản phẩm theo ID.
    ProductResponse getById(Integer id);

    // Lấy danh sách tất cả sản phẩm.
    List<ProductResponse> getAll();
}
