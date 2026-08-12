package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.response.InventorySummaryResponse;
import com.vertex.stockflow.dto.response.StocktakeVarianceResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExcelExportServiceTest {

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ExcelExportService excelExportService;

    private final LocalDateTime from = LocalDateTime.of(2026, 8, 1, 0, 0);
    private final LocalDateTime to = LocalDateTime.of(2026, 8, 31, 23, 59, 59);

    @Test
    @DisplayName("Export tạo file xlsx hợp lệ với 2 sheet và dữ liệu đúng")
    void export_producesValidXlsxWithCorrectData() throws Exception {
        InventorySummaryResponse summary = InventorySummaryResponse.builder()
                .productId(1).productCode("SP001").productName("Paracetamol").unit("hộp")
                .lotId(10).lotCode("L001").unitPrice(new BigDecimal("15000"))
                .openingQty(100).openingValue(new BigDecimal("1500000"))
                .inboundQty(50).inboundValue(new BigDecimal("750000"))
                .outboundQty(30).outboundValue(new BigDecimal("450000"))
                .closingQty(120).closingValue(new BigDecimal("1800000"))
                .build();

        StocktakeVarianceResponse variance = StocktakeVarianceResponse.builder()
                .stocktakeId(1).stocktakeCode("KK-000001")
                .warehouseId(1).warehouseCode("KHO01")
                .approvedAt(LocalDateTime.of(2026, 8, 15, 10, 30))
                .productId(1).productCode("SP001").productName("Paracetamol")
                .lotId(10).lotCode("L001")
                .locationId(5).locationCode("A1-01")
                .systemQty(100).actualQty(95).diffQty(-5)
                .note("Hàng bị ẩm")
                .build();

        when(reportService.getInventorySummaryAll(eq(from), eq(to), isNull()))
                .thenReturn(List.of(summary));
        when(reportService.getStocktakeVarianceAll(eq(from), eq(to)))
                .thenReturn(List.of(variance));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        excelExportService.exportInventoryReport(from, to, null, out);

        byte[] bytes = out.toByteArray();
        assertTrue(bytes.length > 0, "File không được rỗng");

        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            assertEquals(2, wb.getNumberOfSheets());

            // Sheet 1: Nhập-Xuất-Tồn
            Sheet sheet1 = wb.getSheet("Nhập-Xuất-Tồn");
            assertNotNull(sheet1, "Sheet 'Nhập-Xuất-Tồn' phải tồn tại");
            assertEquals(2, sheet1.getPhysicalNumberOfRows()); // 1 header + 1 data
            Row dataRow = sheet1.getRow(1);
            assertEquals("SP001", dataRow.getCell(0).getStringCellValue());
            assertEquals("Paracetamol", dataRow.getCell(1).getStringCellValue());
            assertEquals(15000.0, dataRow.getCell(4).getNumericCellValue(), 0.01);
            assertEquals(100, (int) dataRow.getCell(5).getNumericCellValue());
            assertEquals(120, (int) dataRow.getCell(11).getNumericCellValue());
            assertEquals(1800000.0, dataRow.getCell(12).getNumericCellValue(), 0.01);

            // Sheet 2: Chênh lệch kiểm kê
            Sheet sheet2 = wb.getSheet("Chênh lệch kiểm kê");
            assertNotNull(sheet2, "Sheet 'Chênh lệch kiểm kê' phải tồn tại");
            assertEquals(2, sheet2.getPhysicalNumberOfRows());
            Row varRow = sheet2.getRow(1);
            assertEquals("KK-000001", varRow.getCell(0).getStringCellValue());
            assertEquals(100, (int) varRow.getCell(7).getNumericCellValue());
            assertEquals(95, (int) varRow.getCell(8).getNumericCellValue());
            assertEquals(-5, (int) varRow.getCell(9).getNumericCellValue());
        }
    }

    @Test
    @DisplayName("Export với dữ liệu rỗng vẫn tạo file hợp lệ chỉ có header")
    void export_emptyData_producesValidXlsxWithHeadersOnly() throws Exception {
        when(reportService.getInventorySummaryAll(eq(from), eq(to), isNull()))
                .thenReturn(Collections.emptyList());
        when(reportService.getStocktakeVarianceAll(eq(from), eq(to)))
                .thenReturn(Collections.emptyList());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        excelExportService.exportInventoryReport(from, to, null, out);

        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(out.toByteArray()))) {
            assertEquals(2, wb.getNumberOfSheets());
            assertEquals(1, wb.getSheet("Nhập-Xuất-Tồn").getPhysicalNumberOfRows()); // header only
            assertEquals(1, wb.getSheet("Chênh lệch kiểm kê").getPhysicalNumberOfRows());
        }
    }

    @Test
    @DisplayName("Export với nhiều dòng dữ liệu")
    void export_multipleRows_allPresent() throws Exception {
        InventorySummaryResponse r1 = InventorySummaryResponse.builder()
                .productId(1).productCode("SP001").productName("A").unit("hộp")
                .lotId(1).lotCode("L001").unitPrice(BigDecimal.TEN)
                .openingQty(10).openingValue(new BigDecimal("100"))
                .inboundQty(5).inboundValue(new BigDecimal("50"))
                .outboundQty(3).outboundValue(new BigDecimal("30"))
                .closingQty(12).closingValue(new BigDecimal("120"))
                .build();
        InventorySummaryResponse r2 = InventorySummaryResponse.builder()
                .productId(2).productCode("SP002").productName("B").unit("viên")
                .lotId(2).lotCode("L002").unitPrice(BigDecimal.ONE)
                .openingQty(0).openingValue(BigDecimal.ZERO)
                .inboundQty(100).inboundValue(new BigDecimal("100"))
                .outboundQty(0).outboundValue(BigDecimal.ZERO)
                .closingQty(100).closingValue(new BigDecimal("100"))
                .build();

        when(reportService.getInventorySummaryAll(eq(from), eq(to), isNull()))
                .thenReturn(Arrays.asList(r1, r2));
        when(reportService.getStocktakeVarianceAll(eq(from), eq(to)))
                .thenReturn(Collections.emptyList());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        excelExportService.exportInventoryReport(from, to, null, out);

        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(out.toByteArray()))) {
            Sheet sheet = wb.getSheet("Nhập-Xuất-Tồn");
            assertEquals(3, sheet.getPhysicalNumberOfRows()); // 1 header + 2 data
            assertEquals("SP001", sheet.getRow(1).getCell(0).getStringCellValue());
            assertEquals("SP002", sheet.getRow(2).getCell(0).getStringCellValue());
        }
    }
}
