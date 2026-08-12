package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.dto.response.InventorySummaryResponse;
import com.vertex.stockflow.dto.response.StocktakeVarianceResponse;
import com.vertex.stockflow.repository.ReportRepository;
import com.vertex.stockflow.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    @Override
    public Page<InventorySummaryResponse> getInventorySummary(LocalDateTime from, LocalDateTime to, Integer productId, Pageable pageable) {
        List<InventorySummaryResponse> all = getInventorySummaryAll(from, to, productId);
        return toPage(all, pageable);
    }

    @Override
    public List<InventorySummaryResponse> getInventorySummaryAll(LocalDateTime from, LocalDateTime to, Integer productId) {
        List<Object[]> rows = reportRepository.findInventorySummary(from, to, productId);
        return rows.stream().map(this::mapInventorySummary).toList();
    }

    @Override
    public Page<StocktakeVarianceResponse> getStocktakeVariance(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        List<StocktakeVarianceResponse> all = getStocktakeVarianceAll(from, to);
        return toPage(all, pageable);
    }

    @Override
    public List<StocktakeVarianceResponse> getStocktakeVarianceAll(LocalDateTime from, LocalDateTime to) {
        List<Object[]> rows = reportRepository.findStocktakeVariance(from, to);
        return rows.stream().map(this::mapStocktakeVariance).toList();
    }

    private <T> Page<T> toPage(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        List<T> subList = start >= list.size() ? List.of() : list.subList(start, end);
        return new PageImpl<>(subList, pageable, list.size());
    }

    private InventorySummaryResponse mapInventorySummary(Object[] row) {
        BigDecimal unitPrice = toBigDecimal(row[6]);
        int openingQty = toInt(row[7]);
        int inboundQty = toInt(row[8]);
        int outboundQty = toInt(row[9]);
        int closingQty = toInt(row[10]);

        return InventorySummaryResponse.builder()
                .productId(toInt(row[0]))
                .productCode((String) row[1])
                .productName((String) row[2])
                .unit((String) row[3])
                .lotId(toInt(row[4]))
                .lotCode((String) row[5])
                .unitPrice(unitPrice)
                .openingQty(openingQty)
                .openingValue(unitPrice.multiply(BigDecimal.valueOf(openingQty)))
                .inboundQty(inboundQty)
                .inboundValue(unitPrice.multiply(BigDecimal.valueOf(inboundQty)))
                .outboundQty(outboundQty)
                .outboundValue(unitPrice.multiply(BigDecimal.valueOf(outboundQty)))
                .closingQty(closingQty)
                .closingValue(unitPrice.multiply(BigDecimal.valueOf(closingQty)))
                .build();
    }

    private StocktakeVarianceResponse mapStocktakeVariance(Object[] row) {
        return StocktakeVarianceResponse.builder()
                .stocktakeId(toInt(row[0]))
                .stocktakeCode((String) row[1])
                .warehouseId(toInt(row[2]))
                .warehouseCode((String) row[3])
                .approvedAt(toLocalDateTime(row[4]))
                .productId(toInt(row[5]))
                .productCode((String) row[6])
                .productName((String) row[7])
                .lotId(toInt(row[8]))
                .lotCode((String) row[9])
                .locationId(toInt(row[10]))
                .locationCode((String) row[11])
                .systemQty(toInt(row[12]))
                .actualQty(toInt(row[13]))
                .diffQty(toInt(row[14]))
                .note((String) row[15])
                .build();
    }

    private int toInt(Object val) {
        if (val == null) return 0;
        return ((Number) val).intValue();
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val == null) return BigDecimal.ZERO;
        if (val instanceof BigDecimal bd) return bd;
        return BigDecimal.valueOf(((Number) val).doubleValue());
    }

    private LocalDateTime toLocalDateTime(Object val) {
        if (val == null) return null;
        if (val instanceof LocalDateTime ldt) return ldt;
        if (val instanceof java.sql.Timestamp ts) return ts.toLocalDateTime();
        return null;
    }
}
