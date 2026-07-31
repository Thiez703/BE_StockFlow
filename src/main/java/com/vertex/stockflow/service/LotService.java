package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.LotCreateRequest;
import com.vertex.stockflow.dto.request.LotUpdateRequest;
import com.vertex.stockflow.dto.request.SupplierRequest;
import com.vertex.stockflow.dto.response.LotResponse;
import com.vertex.stockflow.dto.response.SupplierResponse;

import java.util.List;

public interface LotService {
    LotResponse create(LotCreateRequest request);
    LotResponse update(Integer id, LotUpdateRequest request);
    LotResponse getById(Integer id);
    List<LotResponse> getByProductId(Integer productId);
    List<LotResponse> getAll();
    void delete(Integer id);
}
