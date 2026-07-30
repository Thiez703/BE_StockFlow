package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.SupplierRequest;
import com.vertex.stockflow.dto.response.SupplierResponse;

import java.util.List;

public interface SupplierService {
    SupplierResponse create(SupplierRequest request);
    SupplierResponse update(Integer id, SupplierRequest request);
    SupplierResponse getById(Integer id);
    List<SupplierResponse> getAll();
    SupplierResponse delete(Integer id);
}
