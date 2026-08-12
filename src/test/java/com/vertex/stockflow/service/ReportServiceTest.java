package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.response.InventorySummaryResponse;
import com.vertex.stockflow.dto.response.StocktakeVarianceResponse;
import com.vertex.stockflow.repository.ReportRepository;
import com.vertex.stockflow.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    private LocalDateTime from;
    private LocalDateTime to;

    @BeforeEach
    void setUp() {
        from = LocalDateTime.of(2026, 8, 1, 0, 0);
        to = LocalDateTime.of(2026, 8, 31, 23, 59, 59);
    }

    // =========================================================================
    // Inventory Summary tests
    // =========================================================================

    @Test
    @DisplayName("Inventory summary: tính đúng tồn đầu/nhập/xuất/cuối kỳ và giá trị")
    void inventorySummary_calculatesCorrectly() {
        // Product A, Lot L001: opening=100, inbound=50, outbound=30, closing=120, unitPrice=15000
        Object[] row = new Object[]{
                1, "SP001", "Paracetamol", "hộp",      // product
                10, "L001",                              // lot
                new BigDecimal("15000.00"),               // unitPrice
                100, 50, 30, 120                          // opening, inbound, outbound, closing
        };

        when(reportRepository.findInventorySummary(eq(from), eq(to), isNull()))
                .thenReturn(Collections.singletonList(row));

        List<InventorySummaryResponse> result = reportService.getInventorySummaryAll(from, to, null);

        assertEquals(1, result.size());
        InventorySummaryResponse r = result.get(0);

        assertEquals(1, r.getProductId());
        assertEquals("SP001", r.getProductCode());
        assertEquals("Paracetamol", r.getProductName());
        assertEquals("hộp", r.getUnit());
        assertEquals(10, r.getLotId());
        assertEquals("L001", r.getLotCode());

        // Số lượng
        assertEquals(100, r.getOpeningQty());
        assertEquals(50, r.getInboundQty());
        assertEquals(30, r.getOutboundQty());
        assertEquals(120, r.getClosingQty());

        // Giá trị = qty × unitPrice
        BigDecimal unitPrice = new BigDecimal("15000.00");
        assertEquals(0, unitPrice.multiply(BigDecimal.valueOf(100)).compareTo(r.getOpeningValue()));
        assertEquals(0, unitPrice.multiply(BigDecimal.valueOf(50)).compareTo(r.getInboundValue()));
        assertEquals(0, unitPrice.multiply(BigDecimal.valueOf(30)).compareTo(r.getOutboundValue()));
        assertEquals(0, unitPrice.multiply(BigDecimal.valueOf(120)).compareTo(r.getClosingValue()));
    }

    @Test
    @DisplayName("Inventory summary: nhiều lô cùng sản phẩm trả đúng từng dòng")
    void inventorySummary_multipleLotsSameProduct() {
        Object[] lot1 = new Object[]{1, "SP001", "Paracetamol", "hộp", 10, "L001",
                new BigDecimal("15000"), 100, 50, 20, 130};
        Object[] lot2 = new Object[]{1, "SP001", "Paracetamol", "hộp", 11, "L002",
                new BigDecimal("16000"), 0, 200, 0, 200};

        when(reportRepository.findInventorySummary(eq(from), eq(to), eq(1)))
                .thenReturn(Arrays.asList(lot1, lot2));

        List<InventorySummaryResponse> result = reportService.getInventorySummaryAll(from, to, 1);

        assertEquals(2, result.size());
        assertEquals("L001", result.get(0).getLotCode());
        assertEquals("L002", result.get(1).getLotCode());

        // Lot 1: closing = 130
        assertEquals(130, result.get(0).getClosingQty());
        // Lot 2: closing = 200
        assertEquals(200, result.get(1).getClosingQty());
    }

    @Test
    @DisplayName("Inventory summary: closing = opening + inbound - outbound (đối chiếu chéo)")
    void inventorySummary_crossCheckClosingEqualsOpeningPlusNetChange() {
        Object[] row = new Object[]{2, "SP002", "Amoxicillin", "viên", 20, "L010",
                new BigDecimal("5000"), 500, 150, 80, 570};

        when(reportRepository.findInventorySummary(eq(from), eq(to), isNull()))
                .thenReturn(Collections.singletonList(row));

        List<InventorySummaryResponse> result = reportService.getInventorySummaryAll(from, to, null);
        InventorySummaryResponse r = result.get(0);

        // Kiểm tra: closingQty = openingQty + inboundQty - outboundQty
        int expectedClosing = r.getOpeningQty() + r.getInboundQty() - r.getOutboundQty();
        assertEquals(expectedClosing, r.getClosingQty(),
                "Tồn cuối kỳ phải = tồn đầu kỳ + nhập - xuất");

        // Kiểm tra giá trị tương ứng
        BigDecimal expectedClosingValue = r.getUnitPrice().multiply(BigDecimal.valueOf(r.getClosingQty()));
        assertEquals(0, expectedClosingValue.compareTo(r.getClosingValue()),
                "Giá trị tồn cuối = closingQty × unitPrice");
    }

    @Test
    @DisplayName("Inventory summary: không có giao dịch nào trả list rỗng")
    void inventorySummary_emptyResult() {
        when(reportRepository.findInventorySummary(eq(from), eq(to), isNull()))
                .thenReturn(Collections.emptyList());

        List<InventorySummaryResponse> result = reportService.getInventorySummaryAll(from, to, null);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Inventory summary: unitPrice null thì value = 0")
    void inventorySummary_nullUnitPriceTreatedAsZero() {
        Object[] row = new Object[]{1, "SP001", "Test", "cái", 10, "L001",
                null, 50, 10, 5, 55};

        when(reportRepository.findInventorySummary(eq(from), eq(to), isNull()))
                .thenReturn(Collections.singletonList(row));

        List<InventorySummaryResponse> result = reportService.getInventorySummaryAll(from, to, null);
        assertEquals(0, BigDecimal.ZERO.compareTo(result.get(0).getClosingValue()));
    }

    // =========================================================================
    // Stocktake Variance tests
    // =========================================================================

    @Test
    @DisplayName("Stocktake variance: trả đúng thông tin chênh lệch")
    void stocktakeVariance_returnsCorrectData() {
        Timestamp approvedAt = Timestamp.valueOf(LocalDateTime.of(2026, 8, 15, 10, 30));
        Object[] row = new Object[]{
                1, "KK-000001",                           // stocktake
                1, "KHO01",                               // warehouse
                approvedAt,                               // approvedAt
                1, "SP001", "Paracetamol",                // product
                10, "L001",                               // lot
                5, "A1-01",                                // location
                100, 95, -5,                               // system, actual, diff
                "Hàng bị ẩm"                              // note
        };

        when(reportRepository.findStocktakeVariance(eq(from), eq(to)))
                .thenReturn(Collections.singletonList(row));

        List<StocktakeVarianceResponse> result = reportService.getStocktakeVarianceAll(from, to);

        assertEquals(1, result.size());
        StocktakeVarianceResponse r = result.get(0);

        assertEquals("KK-000001", r.getStocktakeCode());
        assertEquals("KHO01", r.getWarehouseCode());
        assertEquals("SP001", r.getProductCode());
        assertEquals("L001", r.getLotCode());
        assertEquals("A1-01", r.getLocationCode());
        assertEquals(100, r.getSystemQty());
        assertEquals(95, r.getActualQty());
        assertEquals(-5, r.getDiffQty());
        assertEquals("Hàng bị ẩm", r.getNote());
    }

    @Test
    @DisplayName("Stocktake variance: không có phiếu duyệt trong kỳ → list rỗng")
    void stocktakeVariance_emptyWhenNoApproved() {
        when(reportRepository.findStocktakeVariance(eq(from), eq(to)))
                .thenReturn(Collections.emptyList());

        List<StocktakeVarianceResponse> result = reportService.getStocktakeVarianceAll(from, to);
        assertTrue(result.isEmpty());
    }
}
