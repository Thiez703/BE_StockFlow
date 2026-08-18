package com.vertex.stockflow.service;

import com.vertex.stockflow.common.enums.DocumentStatusEnum;
import com.vertex.stockflow.dto.request.InboundCreateRequest;
import com.vertex.stockflow.dto.response.InboundResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;

public interface InboundService {
    // Tạo phiếu nhập kho mới.
    InboundResponse create(InboundCreateRequest request, User actor);

    // Hủy phiếu nhập kho.
    void voidInbound(Integer id, String reason, User actor);

    // Tìm kiếm danh sách phiếu nhập kho.
    Page<InboundResponse> search(Integer warehouseId, Integer supplierId,
                                  DocumentStatusEnum status,
                                  LocalDateTime from, LocalDateTime to,
                                  Pageable pageable);

    // Lấy thông tin phiếu nhập kho theo ID.
    InboundResponse getById(Integer id);
}
