package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.InventoryTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransactionEntity, Long>,
        JpaSpecificationExecutor<InventoryTransactionEntity> {

    /**
     * Tổng số lượng xuất (OUTBOUND) theo từng sản phẩm trong khoảng thời gian.
     * quantityChange cho OUTBOUND là số âm, nên dùng ABS.
     * Trả về Object[]{productId (Integer), totalOutQty (Long)}.
     */
    @Query(value = """
            SELECT i.product_id, SUM(ABS(t.quantity_change)) AS total_out_qty
            FROM inventory_transactions t
            JOIN inventory i ON i.id = t.inventory_id
            WHERE t.ref_type = 'OUTBOUND'
              AND t.created_at >= :since
            GROUP BY i.product_id
            """, nativeQuery = true)
    List<Object[]> sumOutboundQtyByProductSince(@Param("since") LocalDateTime since);
}
