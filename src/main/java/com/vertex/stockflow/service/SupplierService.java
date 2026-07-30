package com.vertex.stockflow.service;

import com.vertex.stockflow.common.enums.StatusEnum;
import com.vertex.stockflow.dto.response.PageReponse;
import com.vertex.stockflow.dto.request.SupplierRequest;
import com.vertex.stockflow.dto.response.SupplierReponse;

import org.springframework.data.domain.Pageable;

public interface SupplierService {
    SupplierReponse create(SupplierRequest request);
    SupplierReponse update(Integer id, SupplierRequest request);
    SupplierReponse getById(Integer id);
    PageReponse<SupplierReponse> search(String keyword, StatusEnum status, Pageable pageable);
    SupplierReponse delete(Integer id);
}
