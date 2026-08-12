package com.vertex.stockflow.controller;

import com.vertex.stockflow.dto.response.InventorySummaryResponse;
import com.vertex.stockflow.dto.response.StocktakeVarianceResponse;
import com.vertex.stockflow.service.ExcelExportService;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','ACCOUNTANT')")
public class ReportController {

    private final ReportService reportService;
    private final ExcelExportService excelExportService;

    @GetMapping("/inventory-summary")
    public ResponseEntity<Page<InventorySummaryResponse>> getInventorySummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) Integer productId,
            Pageable pageable) {
        return ResponseEntity.ok(reportService.getInventorySummary(from, to, productId, pageable));
    }

    @GetMapping("/inventory-summary/export")
    public void exportInventorySummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) Integer productId,
            HttpServletResponse response) throws IOException {

        String filename = "BaoCao_NhapXuatTon_" + from.format(DateTimeFormatter.BASIC_ISO_DATE)
                + "_" + to.format(DateTimeFormatter.BASIC_ISO_DATE) + ".xlsx";

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

        excelExportService.exportInventoryReport(from, to, productId, response.getOutputStream());
    }

    @GetMapping("/stocktake-variance")
    public ResponseEntity<Page<StocktakeVarianceResponse>> getStocktakeVariance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Pageable pageable) {
        return ResponseEntity.ok(reportService.getStocktakeVariance(from, to, pageable));
    }
}
