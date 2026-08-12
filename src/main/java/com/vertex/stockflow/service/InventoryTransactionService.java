package com.vertex.stockflow.service;

import com.vertex.stockflow.common.enums.RefTypeEnum;
import com.vertex.stockflow.dto.response.InventoryTransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface InventoryTransactionService {

    Page<InventoryTransactionResponse> search(Integer productId, Integer lotId, Integer locationId,
                                               LocalDateTime from, LocalDateTime to,
                                               RefTypeEnum refType, Pageable pageable);

    Page<InventoryTransactionResponse> getByProduct(Integer productId, Pageable pageable);
}
