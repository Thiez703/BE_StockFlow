package com.vertex.stockflow.service;

import com.vertex.stockflow.common.enums.DocumentStatusEnum;
import com.vertex.stockflow.dto.request.InboundCreateRequest;
import com.vertex.stockflow.dto.response.InboundResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;

public interface InboundService {
    InboundResponse create(InboundCreateRequest request, User actor);
    void voidInbound(Integer id, String reason, User actor);
    Page<InboundResponse> search(Integer warehouseId, Integer supplierId,
                                  DocumentStatusEnum status,
                                  LocalDateTime from, LocalDateTime to,
                                  Pageable pageable);
    InboundResponse getById(Integer id);
}
