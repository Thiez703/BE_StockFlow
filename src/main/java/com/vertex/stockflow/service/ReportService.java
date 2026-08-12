package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.response.InventorySummaryResponse;
import com.vertex.stockflow.dto.response.StocktakeVarianceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportService {

    Page<InventorySummaryResponse> getInventorySummary(LocalDateTime from, LocalDateTime to, Integer productId, Pageable pageable);

    List<InventorySummaryResponse> getInventorySummaryAll(LocalDateTime from, LocalDateTime to, Integer productId);

    Page<StocktakeVarianceResponse> getStocktakeVariance(LocalDateTime from, LocalDateTime to, Pageable pageable);

    List<StocktakeVarianceResponse> getStocktakeVarianceAll(LocalDateTime from, LocalDateTime to);
}
