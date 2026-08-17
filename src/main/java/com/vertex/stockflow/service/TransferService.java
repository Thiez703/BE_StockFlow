package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.TransferCreateRequest;
import com.vertex.stockflow.dto.response.TransferResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;

public interface TransferService {
    TransferResponse create(TransferCreateRequest request, User actor);
    TransferResponse getById(Integer id);
    Page<TransferResponse> search(Integer warehouseId, Pageable pageable);
}
