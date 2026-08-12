package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.response.InventorySummaryResponse;
import com.vertex.stockflow.dto.response.StocktakeVarianceResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelExportService {

    private static final DateTimeFormatter VN_DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ReportService reportService;

    public void exportInventoryReport(LocalDateTime from, LocalDateTime to, Integer productId, OutputStream out) throws IOException {
        List<InventorySummaryResponse> summaryData = reportService.getInventorySummaryAll(from, to, productId);
        List<StocktakeVarianceResponse> varianceData = reportService.getStocktakeVarianceAll(from, to);

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);

            buildSummarySheet(workbook, summaryData, headerStyle, numberStyle, currencyStyle);
            buildVarianceSheet(workbook, varianceData, headerStyle, numberStyle);

            workbook.write(out);
        }
    }

    private void buildSummarySheet(SXSSFWorkbook workbook, List<InventorySummaryResponse> data,
                                   CellStyle headerStyle, CellStyle numberStyle, CellStyle currencyStyle) {
        Sheet sheet = workbook.createSheet("Nhập-Xuất-Tồn");

        String[] headers = {
                "Mã SP", "Tên SP", "ĐVT", "Mã lô", "Đơn giá",
                "SL đầu kỳ", "Giá trị đầu kỳ",
                "SL nhập", "Giá trị nhập",
                "SL xuất", "Giá trị xuất",
                "SL cuối kỳ", "Giá trị cuối kỳ"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        for (InventorySummaryResponse r : data) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(r.getProductCode());
            row.createCell(1).setCellValue(r.getProductName());
            row.createCell(2).setCellValue(r.getUnit());
            row.createCell(3).setCellValue(r.getLotCode());

            setCurrencyCell(row, 4, r.getUnitPrice().doubleValue(), currencyStyle);
            setNumberCell(row, 5, r.getOpeningQty(), numberStyle);
            setCurrencyCell(row, 6, r.getOpeningValue().doubleValue(), currencyStyle);
            setNumberCell(row, 7, r.getInboundQty(), numberStyle);
            setCurrencyCell(row, 8, r.getInboundValue().doubleValue(), currencyStyle);
            setNumberCell(row, 9, r.getOutboundQty(), numberStyle);
            setCurrencyCell(row, 10, r.getOutboundValue().doubleValue(), currencyStyle);
            setNumberCell(row, 11, r.getClosingQty(), numberStyle);
            setCurrencyCell(row, 12, r.getClosingValue().doubleValue(), currencyStyle);
        }
    }

    private void buildVarianceSheet(SXSSFWorkbook workbook, List<StocktakeVarianceResponse> data,
                                    CellStyle headerStyle, CellStyle numberStyle) {
        Sheet sheet = workbook.createSheet("Chênh lệch kiểm kê");

        String[] headers = {
                "Mã phiếu KK", "Mã kho", "Ngày duyệt",
                "Mã SP", "Tên SP", "Mã lô", "Mã vị trí",
                "SL hệ thống", "SL thực tế", "Chênh lệch", "Ghi chú"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        for (StocktakeVarianceResponse r : data) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(r.getStocktakeCode());
            row.createCell(1).setCellValue(r.getWarehouseCode());
            row.createCell(2).setCellValue(r.getApprovedAt() != null ? r.getApprovedAt().format(VN_DATE_FMT) : "");
            row.createCell(3).setCellValue(r.getProductCode());
            row.createCell(4).setCellValue(r.getProductName());
            row.createCell(5).setCellValue(r.getLotCode());
            row.createCell(6).setCellValue(r.getLocationCode());
            setNumberCell(row, 7, r.getSystemQty(), numberStyle);
            setNumberCell(row, 8, r.getActualQty(), numberStyle);
            setNumberCell(row, 9, r.getDiffQty(), numberStyle);
            row.createCell(10).setCellValue(r.getNote() != null ? r.getNote() : "");
        }
    }

    private void setNumberCell(Row row, int col, int value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void setCurrencyCell(Row row, int col, double value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private CellStyle createHeaderStyle(SXSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private CellStyle createNumberStyle(SXSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        return style;
    }

    private CellStyle createCurrencyStyle(SXSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
        return style;
    }
}
