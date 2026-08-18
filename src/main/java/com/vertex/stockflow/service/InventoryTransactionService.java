package com.vertex.stockflow.service;

import com.vertex.stockflow.common.enums.RefTypeEnum;
import com.vertex.stockflow.dto.response.InventoryTransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface InventoryTransactionService {
    // Tìm kiếm lịch sử giao dịch tồn kho theo các tiêu chí.
    Page<InventoryTransactionResponse> search(Integer productId, Integer lotId, Integer locationId,
                                               LocalDateTime from, LocalDateTime to,
                                               RefTypeEnum refType, Pageable pageable);

    // Lấy lịch sử giao dịch tồn kho của một sản phẩm.
    Page<InventoryTransactionResponse> getByProduct(Integer productId, Pageable pageable);
}
