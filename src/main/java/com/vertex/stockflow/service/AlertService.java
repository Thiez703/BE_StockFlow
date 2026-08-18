package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.response.LowStockAlertResponse;
import com.vertex.stockflow.dto.response.OutOfStockAlertResponse;
import com.vertex.stockflow.dto.response.SellThroughRiskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AlertService {

    // Lấy danh sách cảnh báo tồn kho thấp.
    Page<LowStockAlertResponse> getLowStockAlerts(Pageable pageable);

    // Lấy danh sách cảnh báo hết hàng.
    Page<OutOfStockAlertResponse> getOutOfStockAlerts(Pageable pageable);

    // Lấy danh sách nguy cơ chậm luân chuyển (bán chậm).
    Page<SellThroughRiskResponse> getSellThroughRisks(Pageable pageable);

    // Lấy thông tin nguy cơ chậm luân chuyển của một lô cụ thể.
    SellThroughRiskResponse getLotSellThroughRisk(Integer lotId);
}
