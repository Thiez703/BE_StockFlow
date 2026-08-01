package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.ProductRequest;
import com.vertex.stockflow.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse create(ProductRequest request);
    ProductResponse update(Integer id, ProductRequest request);
    void delete(Integer id);
    ProductResponse getById(Integer id);
    List<ProductResponse> getAll();
}
