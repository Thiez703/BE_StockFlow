package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.OutboundCreateRequest;
import com.vertex.stockflow.dto.response.OutboundResponse;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface OutboundService {
    OutboundResponse create(OutboundCreateRequest request, User actor);
    void voidOutbound(Integer id, String reason, User actor);
    List<OutboundResponse> getByWarehouseId(Integer warehouseId);
    OutboundResponse getById(Integer id);
}