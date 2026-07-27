package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.WarehouseCreateRequest;
import com.vertex.stockflow.dto.request.WarehouseUpdateRequest;
import com.vertex.stockflow.dto.response.WarehouseResponse;

import java.util.List;

public interface WarehouseService {
    WarehouseResponse create(WarehouseCreateRequest request);
    WarehouseResponse update(Integer id, WarehouseUpdateRequest request);
    void delete(Integer id);
    WarehouseResponse getById(Integer id);
    List<WarehouseResponse> getAll();
}
