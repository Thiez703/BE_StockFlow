package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.OutboundCreateRequest;
import com.vertex.stockflow.dto.response.OutboundResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;

public interface OutboundService {
    // Tạo phiếu xuất kho mới.
    OutboundResponse create(OutboundCreateRequest request, User actor);

    // Hủy phiếu xuất kho.
    void voidOutbound(Integer id, String reason, User actor);

    // Lấy danh sách phiếu xuất kho theo kho.
    Page<OutboundResponse> getByWarehouseId(Integer warehouseId, Pageable pageable);

    // Lấy thông tin phiếu xuất kho theo ID.
    OutboundResponse getById(Integer id);
}