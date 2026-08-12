package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.StatusEnum;
import com.vertex.stockflow.dto.response.LowStockAlertResponse;
import com.vertex.stockflow.dto.response.OutOfStockAlertResponse;
import com.vertex.stockflow.dto.response.SellThroughRiskResponse;
import com.vertex.stockflow.entity.LotEntity;
import com.vertex.stockflow.entity.ProductEntity;
import com.vertex.stockflow.repository.InventoryRepository;
import com.vertex.stockflow.repository.InventoryTransactionRepository;
import com.vertex.stockflow.repository.LotRepository;
import com.vertex.stockflow.repository.ProductRepository;
import com.vertex.stockflow.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertServiceImpl implements AlertService {

    private final InventoryTransactionRepository txnRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final LotRepository lotRepository;

    @Value("${alert.low-stock.safety-days:7}")
    private int safetyDays;

    @Value("${alert.velocity.period-days:30}")
    private int velocityPeriodDays;

    @Override
    public Page<LowStockAlertResponse> getLowStockAlerts(Pageable pageable) {
        Map<Integer, BigDecimal> velocityMap = buildVelocityMap();
        Map<Integer, Long> stockMap = buildStockMap();
        List<ProductEntity> activeProducts = productRepository.findAll().stream()
                .filter(p -> p.getStatus() == StatusEnum.ACTIVE)
                .toList();

        List<LowStockAlertResponse> alerts = new ArrayList<>();

        for (ProductEntity product : activeProducts) {
            int currentStock = stockMap.getOrDefault(product.getId(), 0L).intValue();
            BigDecimal velocity = velocityMap.getOrDefault(product.getId(), BigDecimal.ZERO);

            Integer threshold;
            String source;

            if (product.getMinStock() != null) {
                threshold = product.getMinStock();
                source = "MANUAL";
            } else if (velocity.compareTo(BigDecimal.ZERO) > 0) {
                threshold = velocity.multiply(BigDecimal.valueOf(safetyDays))
                        .setScale(0, RoundingMode.CEILING).intValue();
                source = "AUTO";
            } else {
                continue;
            }

            if (currentStock < threshold) {
                BigDecimal daysRemaining = velocity.compareTo(BigDecimal.ZERO) > 0
                        ? BigDecimal.valueOf(currentStock).divide(velocity, 1, RoundingMode.HALF_UP)
                        : null;

                alerts.add(LowStockAlertResponse.builder()
                        .productId(product.getId())
                        .productCode(product.getCode())
                        .productName(product.getName())
                        .unit(product.getUnit())
                        .currentStock(currentStock)
                        .threshold(threshold)
                        .thresholdSource(source)
                        .velocity30d(velocity)
                        .estimatedDaysRemaining(daysRemaining)
                        .build());
            }
        }

        return toPage(alerts, pageable);
    }

    @Override
    public Page<OutOfStockAlertResponse> getOutOfStockAlerts(Pageable pageable) {
        Map<Integer, BigDecimal> velocityMap = buildVelocityMap();
        Map<Integer, Long> stockMap = buildStockMap();

        List<ProductEntity> activeProducts = productRepository.findAll().stream()
                .filter(p -> p.getStatus() == StatusEnum.ACTIVE)
                .toList();

        List<OutOfStockAlertResponse> alerts = new ArrayList<>();

        for (ProductEntity product : activeProducts) {
            long currentStock = stockMap.getOrDefault(product.getId(), 0L);
            BigDecimal velocity = velocityMap.getOrDefault(product.getId(), BigDecimal.ZERO);

            if (currentStock == 0 && velocity.compareTo(BigDecimal.ZERO) > 0) {
                alerts.add(OutOfStockAlertResponse.builder()
                        .productId(product.getId())
                        .productCode(product.getCode())
                        .productName(product.getName())
                        .unit(product.getUnit())
                        .velocity30d(velocity)
                        .build());
            }
        }

        return toPage(alerts, pageable);
    }

    @Override
    public Page<SellThroughRiskResponse> getSellThroughRisks(Pageable pageable) {
        Map<Integer, BigDecimal> velocityMap = buildVelocityMap();
        LocalDate today = LocalDate.now();

        List<LotEntity> lots = lotRepository.findAll().stream()
                .filter(l -> l.getStatus() == StatusEnum.ACTIVE)
                .filter(l -> l.getExpDate() != null && !l.getExpDate().isBefore(today))
                .toList();

        Map<Integer, Long> lotStockMap = buildLotStockMap();

        List<SellThroughRiskResponse> risks = new ArrayList<>();

        for (LotEntity lot : lots) {
            long lotStock = lotStockMap.getOrDefault(lot.getId(), 0L);
            if (lotStock <= 0) continue;

            BigDecimal velocity = velocityMap.getOrDefault(lot.getProduct().getId(), BigDecimal.ZERO);
            int daysUntilExpiry = (int) ChronoUnit.DAYS.between(today, lot.getExpDate());

            BigDecimal daysToSellOut;
            boolean atRisk;

            if (velocity.compareTo(BigDecimal.ZERO) > 0) {
                daysToSellOut = BigDecimal.valueOf(lotStock).divide(velocity, 1, RoundingMode.HALF_UP);
                atRisk = daysToSellOut.compareTo(BigDecimal.valueOf(daysUntilExpiry)) > 0;
            } else {
                daysToSellOut = null;
                atRisk = true;
            }

            if (atRisk) {
                risks.add(buildSellThroughRiskResponse(lot, (int) lotStock, velocity,
                        daysToSellOut, daysUntilExpiry));
            }
        }

        return toPage(risks, pageable);
    }

    @Override
    public SellThroughRiskResponse getLotSellThroughRisk(Integer lotId) {
        LotEntity lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new RuntimeException("Lot not found: " + lotId));

        Map<Integer, BigDecimal> velocityMap = buildVelocityMap();
        BigDecimal velocity = velocityMap.getOrDefault(lot.getProduct().getId(), BigDecimal.ZERO);

        List<Object[]> lotStockRows = inventoryRepository.sumStockByLotId(lotId);
        long lotStock = lotStockRows.isEmpty() ? 0 : ((Number) lotStockRows.get(0)[1]).longValue();

        LocalDate today = LocalDate.now();
        int daysUntilExpiry = lot.getExpDate() != null
                ? (int) ChronoUnit.DAYS.between(today, lot.getExpDate()) : 0;

        BigDecimal daysToSellOut = null;
        boolean atRisk;

        if (lotStock <= 0) {
            atRisk = false;
        } else if (velocity.compareTo(BigDecimal.ZERO) > 0) {
            daysToSellOut = BigDecimal.valueOf(lotStock).divide(velocity, 1, RoundingMode.HALF_UP);
            atRisk = lot.getExpDate() != null
                    && daysToSellOut.compareTo(BigDecimal.valueOf(daysUntilExpiry)) > 0;
        } else {
            atRisk = lot.getExpDate() != null && daysUntilExpiry >= 0 && lotStock > 0;
        }

        return buildSellThroughRiskResponse(lot, (int) lotStock, velocity, daysToSellOut, daysUntilExpiry);
    }

    // ─── helpers ────────────────────────────────────────────────────────

    private Map<Integer, BigDecimal> buildVelocityMap() {
        LocalDateTime since = LocalDateTime.now().minusDays(velocityPeriodDays);
        List<Object[]> rows = txnRepository.sumOutboundQtyByProductSince(since);

        Map<Integer, BigDecimal> map = new HashMap<>();
        for (Object[] row : rows) {
            Integer productId = ((Number) row[0]).intValue();
            long totalOut = ((Number) row[1]).longValue();
            BigDecimal velocity = BigDecimal.valueOf(totalOut)
                    .divide(BigDecimal.valueOf(velocityPeriodDays), 2, RoundingMode.HALF_UP);
            map.put(productId, velocity);
        }
        return map;
    }

    private Map<Integer, Long> buildStockMap() {
        List<Object[]> rows = inventoryRepository.sumStockByProduct();
        Map<Integer, Long> map = new HashMap<>();
        for (Object[] row : rows) {
            map.put(((Number) row[0]).intValue(), ((Number) row[1]).longValue());
        }
        return map;
    }

    private Map<Integer, Long> buildLotStockMap() {
        List<Object[]> rows = inventoryRepository.sumStockByLot();
        Map<Integer, Long> map = new HashMap<>();
        for (Object[] row : rows) {
            map.put(((Number) row[0]).intValue(), ((Number) row[1]).longValue());
        }
        return map;
    }

    private <T> Page<T> toPage(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        List<T> subList = start >= list.size() ? List.of() : list.subList(start, end);
        return new PageImpl<>(subList, pageable, list.size());
    }

    private SellThroughRiskResponse buildSellThroughRiskResponse(
            LotEntity lot, int lotStock, BigDecimal velocity,
            BigDecimal daysToSellOut, int daysUntilExpiry) {

        boolean atRisk;
        if (lotStock <= 0) {
            atRisk = false;
        } else if (velocity.compareTo(BigDecimal.ZERO) > 0 && daysToSellOut != null) {
            atRisk = daysToSellOut.compareTo(BigDecimal.valueOf(daysUntilExpiry)) > 0;
        } else {
            atRisk = daysUntilExpiry >= 0 && lotStock > 0;
        }

        return SellThroughRiskResponse.builder()
                .lotId(lot.getId())
                .lotCode(lot.getLotCode())
                .productId(lot.getProduct().getId())
                .productCode(lot.getProduct().getCode())
                .productName(lot.getProduct().getName())
                .currentStock(lotStock)
                .velocity30d(velocity)
                .estimatedDaysToSellOut(daysToSellOut)
                .expDate(lot.getExpDate())
                .daysUntilExpiry(daysUntilExpiry)
                .atRisk(atRisk)
                .build();
    }
}
