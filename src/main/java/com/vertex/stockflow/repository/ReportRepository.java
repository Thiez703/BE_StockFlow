package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.InventoryTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<InventoryTransactionEntity, Long> {

    /**
     * Báo cáo nhập-xuất-tồn theo từng lô.
     *
     * - opening_qty: tổng balance_after của giao dịch cuối cùng trước kỳ cho mỗi inventory_id,
     *   gom lại theo product+lot (vì 1 lô có thể nằm ở nhiều vị trí = nhiều inventory record).
     * - inbound_qty / outbound_qty: SUM trong kỳ, tách theo dấu quantity_change.
     * - closing_qty = opening + inbound - outbound.
     * - unit_price: đơn giá nhập gần nhất của lô (từ inbound_details, phiếu POSTED).
     */
    @Query(value = """
            SELECT
                p.id            AS productId,
                p.code          AS productCode,
                p.name          AS productName,
                p.unit          AS unit,
                l.id            AS lotId,
                l.lot_code      AS lotCode,
                COALESCE(lp.unit_price, 0) AS unitPrice,
                COALESCE(ob.opening_qty, 0) AS openingQty,
                COALESCE(sm.inbound_qty, 0) AS inboundQty,
                COALESCE(sm.outbound_qty, 0) AS outboundQty,
                (COALESCE(ob.opening_qty, 0) + COALESCE(sm.inbound_qty, 0) - COALESCE(sm.outbound_qty, 0)) AS closingQty
            FROM lots l
            JOIN products p ON p.id = l.product_id
            LEFT JOIN (
                SELECT id2.lot_id, MAX(id2.unit_price) AS unit_price
                FROM inbound_details id2
                JOIN inbounds ib2 ON ib2.id = id2.inbound_id AND ib2.status = 'POSTED'
                GROUP BY id2.lot_id
            ) lp ON lp.lot_id = l.id
            LEFT JOIN (
                SELECT sub.product_id, sub.lot_id,
                       SUM(sub.last_balance) AS opening_qty
                FROM (
                    SELECT i.product_id, i.lot_id,
                           (SELECT t2.balance_after
                            FROM inventory_transactions t2
                            WHERE t2.inventory_id = i.id
                              AND t2.created_at < :fromDate
                            ORDER BY t2.created_at DESC, t2.id DESC
                            LIMIT 1) AS last_balance
                    FROM inventory i
                ) sub
                WHERE sub.last_balance IS NOT NULL
                GROUP BY sub.product_id, sub.lot_id
            ) ob ON ob.product_id = p.id AND ob.lot_id = l.id
            LEFT JOIN (
                SELECT i.product_id, i.lot_id,
                       SUM(CASE WHEN t.quantity_change > 0 THEN t.quantity_change ELSE 0 END) AS inbound_qty,
                       SUM(CASE WHEN t.quantity_change < 0 THEN ABS(t.quantity_change) ELSE 0 END) AS outbound_qty
                FROM inventory_transactions t
                JOIN inventory i ON i.id = t.inventory_id
                WHERE t.created_at >= :fromDate AND t.created_at <= :toDate
                GROUP BY i.product_id, i.lot_id
            ) sm ON sm.product_id = p.id AND sm.lot_id = l.id
            WHERE (:productId IS NULL OR p.id = :productId)
              AND (ob.opening_qty IS NOT NULL
                   OR sm.inbound_qty IS NOT NULL
                   OR sm.outbound_qty IS NOT NULL)
            ORDER BY p.code, l.lot_code
            """, nativeQuery = true)
    List<Object[]> findInventorySummary(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("productId") Integer productId);

    /**
     * Stocktake variance: phiếu kiểm kê đã duyệt trong kỳ, chi tiết chênh lệch.
     */
    @Query(value = """
            SELECT
                s.id            AS stocktakeId,
                s.code          AS stocktakeCode,
                w.id            AS warehouseId,
                w.code          AS warehouseCode,
                s.approved_at   AS approvedAt,
                p.id            AS productId,
                p.code          AS productCode,
                p.name          AS productName,
                l.id            AS lotId,
                l.lot_code      AS lotCode,
                sl.id           AS locationId,
                sl.location_code AS locationCode,
                sd.system_qty   AS systemQty,
                sd.actual_qty   AS actualQty,
                sd.diff_qty     AS diffQty,
                s.note          AS note
            FROM stocktake_details sd
            JOIN stocktakes s ON s.id = sd.stocktake_id
            JOIN warehouses w ON w.id = s.warehouse_id
            JOIN products p ON p.id = sd.product_id
            JOIN lots l ON l.id = sd.lot_id
            JOIN storage_locations sl ON sl.id = sd.location_id
            WHERE s.status = 'APPROVED'
              AND s.approved_at >= :fromDate
              AND s.approved_at <= :toDate
            ORDER BY s.approved_at DESC, s.code, p.code
            """, nativeQuery = true)
    List<Object[]> findStocktakeVariance(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);
}
