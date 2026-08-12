package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.response.LowStockAlertResponse;
import com.vertex.stockflow.dto.response.OutOfStockAlertResponse;
import com.vertex.stockflow.dto.response.SellThroughRiskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AlertService {

    Page<LowStockAlertResponse> getLowStockAlerts(Pageable pageable);

    Page<OutOfStockAlertResponse> getOutOfStockAlerts(Pageable pageable);

    Page<SellThroughRiskResponse> getSellThroughRisks(Pageable pageable);

    SellThroughRiskResponse getLotSellThroughRisk(Integer lotId);
}
