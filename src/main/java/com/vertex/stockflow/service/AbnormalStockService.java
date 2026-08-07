package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.AbnormalStockCreateRequest;
import com.vertex.stockflow.dto.request.AbnormalStockRejectRequest;
import com.vertex.stockflow.dto.response.AbnormalStockResponse;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface AbnormalStockService {

    AbnormalStockResponse create(AbnormalStockCreateRequest request, User actor);

    List<AbnormalStockResponse> getByWarehouseId(Integer warehouseId);

    AbnormalStockResponse getById(Integer id);

    AbnormalStockResponse approve(Integer id, User actor);

    AbnormalStockResponse reject(Integer id, AbnormalStockRejectRequest request, User actor);
}
