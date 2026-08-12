package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.AbnormalStockCreateRequest;
import com.vertex.stockflow.dto.request.AbnormalStockRejectRequest;
import com.vertex.stockflow.dto.response.AbnormalStockResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;

public interface AbnormalStockService {

    AbnormalStockResponse create(AbnormalStockCreateRequest request, User actor);

    Page<AbnormalStockResponse> getByWarehouseId(Integer warehouseId, Pageable pageable);

    AbnormalStockResponse getById(Integer id);

    AbnormalStockResponse approve(Integer id, User actor);

    AbnormalStockResponse reject(Integer id, AbnormalStockRejectRequest request, User actor);
}
