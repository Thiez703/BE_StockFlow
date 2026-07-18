package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.ProductRequest;
import com.vertex.stockflow.dto.response.ProductResponse;

import java.util.List;

/**
 * BẢN HỢP ĐỒNG (interface) mô tả các chức năng nghiệp vụ của Product.
 * Controller chỉ phụ thuộc interface này, code thật nằm ở ProductServiceImpl.
 */
public interface ProductService {

    ProductResponse create(ProductRequest request);              // Tạo mới
    List<ProductResponse> getAll();                              // Danh sách (chỉ ACTIVE)
    ProductResponse getById(Integer id);                         // Chi tiết theo id
    ProductResponse update(Integer id, ProductRequest request);  // Cập nhật
    void delete(Integer id);                                     // Xóa mềm
}