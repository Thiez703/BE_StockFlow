package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.ProductUnitRequest;
import com.vertex.stockflow.dto.response.ProductUnitResponse;

import java.util.List;

public interface ProductUnitService {

    ProductUnitResponse create(Integer productId , ProductUnitRequest request);

    ProductUnitResponse update(Integer productId, Integer id, ProductUnitRequest request);

    List<ProductUnitResponse> getByProductId(Integer productId);

    void delete(Integer productId, Integer id);
    
}
