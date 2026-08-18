package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.CategoryRequest;
import com.vertex.stockflow.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    // Tạo danh mục mới.
    CategoryResponse create(CategoryRequest request);

    // Cập nhật thông tin danh mục.
    CategoryResponse update(Integer id, CategoryRequest request);

    // Xóa danh mục.
    void delete(Integer id);

    // Vô hiệu hóa danh mục.
    CategoryResponse deactivate(Integer id);

    // Kích hoạt danh mục.
    CategoryResponse activate(Integer id);

    // Lấy danh mục theo ID.
    CategoryResponse getById(Integer id);

    // Lấy danh sách tất cả danh mục.
    List<CategoryResponse> getAll();

    // Lấy cấu trúc cây danh mục.
    List<CategoryResponse> getTree();
}
