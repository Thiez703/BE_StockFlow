package com.vertex.stockflow.controller;

import com.vertex.stockflow.dto.response.LowStockAlertResponse;
import com.vertex.stockflow.dto.response.OutOfStockAlertResponse;
import com.vertex.stockflow.dto.response.SellThroughRiskResponse;
import com.vertex.stockflow.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','ACCOUNTANT')")
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/low-stock")
    public ResponseEntity<Page<LowStockAlertResponse>> getLowStock(Pageable pageable) {
        return ResponseEntity.ok(alertService.getLowStockAlerts(pageable));
    }

    @GetMapping("/out-of-stock")
    public ResponseEntity<Page<OutOfStockAlertResponse>> getOutOfStock(Pageable pageable) {
        return ResponseEntity.ok(alertService.getOutOfStockAlerts(pageable));
    }

    @GetMapping("/sell-through-risk")
    public ResponseEntity<Page<SellThroughRiskResponse>> getSellThroughRisks(Pageable pageable) {
        return ResponseEntity.ok(alertService.getSellThroughRisks(pageable));
    }
}
