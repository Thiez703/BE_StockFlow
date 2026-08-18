package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.response.InventorySummaryResponse;
import com.vertex.stockflow.dto.response.StocktakeVarianceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportService {
    // Báo cáo tổng hợp tồn kho (có phân trang).
    Page<InventorySummaryResponse> getInventorySummary(LocalDateTime from, LocalDateTime to, Integer productId, Pageable pageable);

    // Báo cáo tổng hợp tồn kho (không phân trang).
    List<InventorySummaryResponse> getInventorySummaryAll(LocalDateTime from, LocalDateTime to, Integer productId);

    // Báo cáo chênh lệch kiểm kê (có phân trang).
    Page<StocktakeVarianceResponse> getStocktakeVariance(LocalDateTime from, LocalDateTime to, Pageable pageable);

    // Báo cáo chênh lệch kiểm kê (không phân trang).
    List<StocktakeVarianceResponse> getStocktakeVarianceAll(LocalDateTime from, LocalDateTime to);
}
