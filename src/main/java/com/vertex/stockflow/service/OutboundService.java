package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.OutboundCreateRequest;
import com.vertex.stockflow.dto.response.OutboundResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;

public interface OutboundService {
    OutboundResponse create(OutboundCreateRequest request, User actor);
    void voidOutbound(Integer id, String reason, User actor);
    Page<OutboundResponse> getByWarehouseId(Integer warehouseId, Pageable pageable);
    OutboundResponse getById(Integer id);
}