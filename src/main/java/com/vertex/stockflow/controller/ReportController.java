package com.vertex.stockflow.controller;

import com.vertex.stockflow.dto.response.InventorySummaryResponse;
import com.vertex.stockflow.dto.response.StocktakeVarianceResponse;
import com.vertex.stockflow.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','ACCOUNTANT')")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/inventory-summary")
    public ResponseEntity<Page<InventorySummaryResponse>> getInventorySummary(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(required = false) Integer productId,
            Pageable pageable) {
        LocalDateTime fromDt = parseDate(from).atStartOfDay();
        LocalDateTime toDt = parseDate(to).atTime(LocalTime.MAX);
        return ResponseEntity.ok(reportService.getInventorySummary(fromDt, toDt, productId, pageable));
    }

    @GetMapping("/stocktake-variance")
    public ResponseEntity<Page<StocktakeVarianceResponse>> getStocktakeVariance(
            @RequestParam String from,
            @RequestParam String to,
            Pageable pageable) {
        LocalDateTime fromDt = parseDate(from).atStartOfDay();
        LocalDateTime toDt = parseDate(to).atTime(LocalTime.MAX);
        return ResponseEntity.ok(reportService.getStocktakeVariance(fromDt, toDt, pageable));
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return LocalDate.now();
        if (dateStr.contains("T")) {
            return java.time.ZonedDateTime.parse(dateStr)
                    .withZoneSameInstant(java.time.ZoneId.systemDefault())
                    .toLocalDate();
        }
        return LocalDate.parse(dateStr);
    }
}
