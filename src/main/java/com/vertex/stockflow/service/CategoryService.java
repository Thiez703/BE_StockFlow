package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.CategoryRequest;
import com.vertex.stockflow.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest request);
    CategoryResponse update(Integer id, CategoryRequest request);
    void delete(Integer id);
    CategoryResponse getById(Integer id);
    List<CategoryResponse> getAll();
    List<CategoryResponse> getTree();
}
