package com.vertex.stockflow.service;

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
import com.vertex.stockflow.service.impl.AlertServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private InventoryTransactionRepository txnRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private LotRepository lotRepository;

    @InjectMocks
    private AlertServiceImpl alertService;

    private ProductEntity productA;
    private ProductEntity productB;
    private ProductEntity productC;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(alertService, "safetyDays", 7);
        ReflectionTestUtils.setField(alertService, "velocityPeriodDays", 30);

        productA = ProductEntity.builder()
                .id(1).code("SP001").name("Paracetamol").unit("hộp")
                .status(StatusEnum.ACTIVE).minStock(null).build();
        productB = ProductEntity.builder()
                .id(2).code("SP002").name("Amoxicillin").unit("viên")
                .status(StatusEnum.ACTIVE).minStock(200).build();
        productC = ProductEntity.builder()
                .id(3).code("SP003").name("Vitamin C").unit("lọ")
                .status(StatusEnum.ACTIVE).minStock(null).build();
    }

    // =========================================================================
    // Low-stock alerts
    // =========================================================================
    @Nested
    @DisplayName("Low-stock alerts")
    class LowStockTests {

        @Test
        @DisplayName("Sản phẩm có velocity > 0 và tồn dưới ngưỡng tự tính → xuất hiện trong low-stock")
        void autoThreshold_belowMinShowsAlert() {
            // velocity = 300/30 = 10/day, threshold = 10 × 7 = 70
            // stock = 50 < 70 → alert
            when(txnRepository.sumOutboundQtyByProductSince(any()))
                    .thenReturn(Collections.singletonList(new Object[]{1, 300L}));
            when(inventoryRepository.sumStockByProduct())
                    .thenReturn(Collections.singletonList(new Object[]{1, 50L}));
            when(productRepository.findAll()).thenReturn(List.of(productA));

            List<LowStockAlertResponse> alerts = alertService.getLowStockAlerts(PageRequest.of(0, 100)).getContent();

            assertEquals(1, alerts.size());
            LowStockAlertResponse alert = alerts.get(0);
            assertEquals("SP001", alert.getProductCode());
            assertEquals(50, alert.getCurrentStock());
            assertEquals(70, alert.getThreshold());
            assertEquals("AUTO", alert.getThresholdSource());
            assertEquals(0, new BigDecimal("10.00").compareTo(alert.getVelocity30d()));
            // daysRemaining = 50/10 = 5.0
            assertEquals(0, new BigDecimal("5.0").compareTo(alert.getEstimatedDaysRemaining()));
        }

        @Test
        @DisplayName("Sản phẩm có min_stock khai báo → dùng min_stock bất kể velocity")
        void manualMinStock_usedRegardlessOfVelocity() {
            // productB minStock=200, velocity=0 (no outbound), stock=100 < 200
            when(txnRepository.sumOutboundQtyByProductSince(any()))
                    .thenReturn(Collections.emptyList());
            when(inventoryRepository.sumStockByProduct())
                    .thenReturn(Collections.singletonList(new Object[]{2, 100L}));
            when(productRepository.findAll()).thenReturn(List.of(productB));

            List<LowStockAlertResponse> alerts = alertService.getLowStockAlerts(PageRequest.of(0, 100)).getContent();

            assertEquals(1, alerts.size());
            LowStockAlertResponse alert = alerts.get(0);
            assertEquals("SP002", alert.getProductCode());
            assertEquals(200, alert.getThreshold());
            assertEquals("MANUAL", alert.getThresholdSource());
            assertNull(alert.getEstimatedDaysRemaining()); // velocity=0 → can't estimate
        }

        @Test
        @DisplayName("Sản phẩm không có min_stock và velocity=0 → không cảnh báo")
        void noMinStockNoVelocity_noAlert() {
            when(txnRepository.sumOutboundQtyByProductSince(any()))
                    .thenReturn(Collections.emptyList());
            when(inventoryRepository.sumStockByProduct())
                    .thenReturn(Collections.singletonList(new Object[]{3, 10L}));
            when(productRepository.findAll()).thenReturn(List.of(productC));

            List<LowStockAlertResponse> alerts = alertService.getLowStockAlerts(PageRequest.of(0, 100)).getContent();
            assertTrue(alerts.isEmpty());
        }

        @Test
        @DisplayName("Sản phẩm có velocity nhưng tồn >= ngưỡng → không cảnh báo")
        void aboveAutoThreshold_noAlert() {
            // velocity = 10/day, threshold = 70, stock = 100 >= 70 → no alert
            when(txnRepository.sumOutboundQtyByProductSince(any()))
                    .thenReturn(Collections.singletonList(new Object[]{1, 300L}));
            when(inventoryRepository.sumStockByProduct())
                    .thenReturn(Collections.singletonList(new Object[]{1, 100L}));
            when(productRepository.findAll()).thenReturn(List.of(productA));

            List<LowStockAlertResponse> alerts = alertService.getLowStockAlerts(PageRequest.of(0, 100)).getContent();
            assertTrue(alerts.isEmpty());
        }

        @Test
        @DisplayName("Sản phẩm INACTIVE bị loại trừ")
        void inactiveProduct_excluded() {
            productA.setStatus(StatusEnum.INACTIVE);

            when(txnRepository.sumOutboundQtyByProductSince(any()))
                    .thenReturn(Collections.singletonList(new Object[]{1, 300L}));
            when(inventoryRepository.sumStockByProduct())
                    .thenReturn(Collections.singletonList(new Object[]{1, 5L}));
            when(productRepository.findAll()).thenReturn(List.of(productA));

            List<LowStockAlertResponse> alerts = alertService.getLowStockAlerts(PageRequest.of(0, 100)).getContent();
            assertTrue(alerts.isEmpty());
        }
    }

    // =========================================================================
    // Out-of-stock alerts
    // =========================================================================
    @Nested
    @DisplayName("Out-of-stock alerts")
    class OutOfStockTests {

        @Test
        @DisplayName("Tồn = 0, velocity > 0 → xuất hiện trong out-of-stock")
        void zeroStockWithVelocity_showsAlert() {
            when(txnRepository.sumOutboundQtyByProductSince(any()))
                    .thenReturn(Collections.singletonList(new Object[]{1, 150L}));
            when(inventoryRepository.sumStockByProduct())
                    .thenReturn(Collections.emptyList()); // no stock rows → 0
            when(productRepository.findAll()).thenReturn(List.of(productA));

            List<OutOfStockAlertResponse> alerts = alertService.getOutOfStockAlerts(PageRequest.of(0, 100)).getContent();

            assertEquals(1, alerts.size());
            assertEquals("SP001", alerts.get(0).getProductCode());
        }

        @Test
        @DisplayName("Tồn = 0, velocity = 0 (ngừng bán) → không cảnh báo")
        void zeroStockNoVelocity_noAlert() {
            when(txnRepository.sumOutboundQtyByProductSince(any()))
                    .thenReturn(Collections.emptyList());
            when(inventoryRepository.sumStockByProduct())
                    .thenReturn(Collections.emptyList());
            when(productRepository.findAll()).thenReturn(List.of(productA));

            List<OutOfStockAlertResponse> alerts = alertService.getOutOfStockAlerts(PageRequest.of(0, 100)).getContent();
            assertTrue(alerts.isEmpty());
        }

        @Test
        @DisplayName("Tồn > 0 → không xuất hiện trong out-of-stock")
        void hasStock_noAlert() {
            when(txnRepository.sumOutboundQtyByProductSince(any()))
                    .thenReturn(Collections.singletonList(new Object[]{1, 100L}));
            when(inventoryRepository.sumStockByProduct())
                    .thenReturn(Collections.singletonList(new Object[]{1, 50L}));
            when(productRepository.findAll()).thenReturn(List.of(productA));

            List<OutOfStockAlertResponse> alerts = alertService.getOutOfStockAlerts(PageRequest.of(0, 100)).getContent();
            assertTrue(alerts.isEmpty());
        }
    }

    // =========================================================================
    // Sell-through risk
    // =========================================================================
    @Nested
    @DisplayName("Sell-through risk")
    class SellThroughRiskTests {

        @Test
        @DisplayName("Lô bán hết dự kiến sau HSD → gắn cờ atRisk")
        void sellOutAfterExpiry_flagged() {
            // velocity = 300/30 = 10/day, lotStock = 500, daysToSellOut = 50
            // expDate = today + 30 → daysUntilExpiry = 30
            // 50 > 30 → at risk
            LotEntity lot = LotEntity.builder()
                    .id(100).lotCode("L001").status(StatusEnum.ACTIVE)
                    .expDate(LocalDate.now().plusDays(30))
                    .product(productA).build();

            when(txnRepository.sumOutboundQtyByProductSince(any()))
                    .thenReturn(Collections.singletonList(new Object[]{1, 300L}));
            when(inventoryRepository.sumStockByLot())
                    .thenReturn(Collections.singletonList(new Object[]{100, 500L}));
            when(lotRepository.findAll()).thenReturn(List.of(lot));

            List<SellThroughRiskResponse> risks = alertService.getSellThroughRisks(PageRequest.of(0, 100)).getContent();

            assertEquals(1, risks.size());
            assertTrue(risks.get(0).isAtRisk());
            assertEquals("L001", risks.get(0).getLotCode());
            assertEquals(0, new BigDecimal("50.0").compareTo(risks.get(0).getEstimatedDaysToSellOut()));
        }

        @Test
        @DisplayName("Lô bán hết trước HSD → không gắn cờ")
        void sellOutBeforeExpiry_notFlagged() {
            // velocity = 10/day, lotStock = 100, daysToSellOut = 10
            // expDate = today + 60 → daysUntilExpiry = 60
            // 10 < 60 → not at risk
            LotEntity lot = LotEntity.builder()
                    .id(101).lotCode("L002").status(StatusEnum.ACTIVE)
                    .expDate(LocalDate.now().plusDays(60))
                    .product(productA).build();

            when(txnRepository.sumOutboundQtyByProductSince(any()))
                    .thenReturn(Collections.singletonList(new Object[]{1, 300L}));
            when(inventoryRepository.sumStockByLot())
                    .thenReturn(Collections.singletonList(new Object[]{101, 100L}));
            when(lotRepository.findAll()).thenReturn(List.of(lot));

            List<SellThroughRiskResponse> risks = alertService.getSellThroughRisks(PageRequest.of(0, 100)).getContent();
            assertTrue(risks.isEmpty());
        }

        @Test
        @DisplayName("Velocity = 0, còn hàng, chưa hết hạn → at risk (không bán được)")
        void zeroVelocityWithStock_atRisk() {
            LotEntity lot = LotEntity.builder()
                    .id(102).lotCode("L003").status(StatusEnum.ACTIVE)
                    .expDate(LocalDate.now().plusDays(90))
                    .product(productC).build();

            when(txnRepository.sumOutboundQtyByProductSince(any()))
                    .thenReturn(Collections.emptyList());
            when(inventoryRepository.sumStockByLot())
                    .thenReturn(Collections.singletonList(new Object[]{102, 200L}));
            when(lotRepository.findAll()).thenReturn(List.of(lot));

            List<SellThroughRiskResponse> risks = alertService.getSellThroughRisks(PageRequest.of(0, 100)).getContent();

            assertEquals(1, risks.size());
            assertTrue(risks.get(0).isAtRisk());
            assertNull(risks.get(0).getEstimatedDaysToSellOut());
        }

        @Test
        @DisplayName("Lô đã hết hạn → không xuất hiện (chỉ cảnh báo lô chưa hết hạn)")
        void expiredLot_excluded() {
            LotEntity lot = LotEntity.builder()
                    .id(103).lotCode("L004").status(StatusEnum.ACTIVE)
                    .expDate(LocalDate.now().minusDays(1))
                    .product(productA).build();

            when(txnRepository.sumOutboundQtyByProductSince(any()))
                    .thenReturn(Collections.singletonList(new Object[]{1, 300L}));
            when(inventoryRepository.sumStockByLot())
                    .thenReturn(Collections.singletonList(new Object[]{103, 100L}));
            when(lotRepository.findAll()).thenReturn(List.of(lot));

            List<SellThroughRiskResponse> risks = alertService.getSellThroughRisks(PageRequest.of(0, 100)).getContent();
            assertTrue(risks.isEmpty());
        }
    }

    // =========================================================================
    // Single-lot sell-through risk (inline endpoint)
    // =========================================================================
    @Nested
    @DisplayName("Single-lot sell-through risk")
    class SingleLotRiskTests {

        @Test
        @DisplayName("GET /api/lots/{lotId}/sell-through-risk trả đúng thông tin")
        void singleLotRisk_returnsCorrectData() {
            LotEntity lot = LotEntity.builder()
                    .id(100).lotCode("L001").status(StatusEnum.ACTIVE)
                    .expDate(LocalDate.now().plusDays(20))
                    .product(productA).build();

            when(lotRepository.findById(100)).thenReturn(Optional.of(lot));
            when(txnRepository.sumOutboundQtyByProductSince(any()))
                    .thenReturn(Collections.singletonList(new Object[]{1, 300L}));
            when(inventoryRepository.sumStockByLotId(100))
                    .thenReturn(Collections.singletonList(new Object[]{100, 300L}));

            SellThroughRiskResponse response = alertService.getLotSellThroughRisk(100);

            assertEquals("L001", response.getLotCode());
            assertEquals(300, response.getCurrentStock());
            // velocity = 10/day, daysToSellOut = 300/10 = 30, daysUntilExpiry = 20
            // 30 > 20 → at risk
            assertTrue(response.isAtRisk());
            assertEquals(0, new BigDecimal("30.0").compareTo(response.getEstimatedDaysToSellOut()));
            assertEquals(20, response.getDaysUntilExpiry());
        }

        @Test
        @DisplayName("Lô không tồn tại → RuntimeException")
        void lotNotFound_throws() {
            when(lotRepository.findById(999)).thenReturn(Optional.empty());
            assertThrows(RuntimeException.class, () -> alertService.getLotSellThroughRisk(999));
        }
    }
}
