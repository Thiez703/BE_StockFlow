package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.TransferCreateRequest;
import com.vertex.stockflow.dto.response.TransferResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;

public interface TransferService {
    // Tạo phiếu chuyển kho.
    TransferResponse create(TransferCreateRequest request, User actor);

    // Lấy chi tiết phiếu chuyển kho theo ID.
    TransferResponse getById(Integer id);

    // Tìm kiếm danh sách phiếu chuyển kho.
    Page<TransferResponse> search(Integer warehouseId, Pageable pageable);
}
