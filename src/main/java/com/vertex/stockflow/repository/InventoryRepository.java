package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryEntity, Integer>, JpaSpecificationExecutor<InventoryEntity> {
    Optional<InventoryEntity> findByWarehouseIdAndProductIdAndLotIdAndLocationId(
            Integer warehouseId, Integer productId, Integer lotId, Integer locationId);

    /**
     * Tổng tồn kho theo sản phẩm (cộng dồn mọi lô, mọi kho).
     * Trả về Object[]{productId (Integer), totalQty (Long)}.
     */
    @Query(value = """
            SELECT i.product_id, SUM(i.quantity) AS total_qty
            FROM inventory i
            GROUP BY i.product_id
            """, nativeQuery = true)
    List<Object[]> sumStockByProduct();

    /**
     * Tổng tồn kho theo lô.
     * Trả về Object[]{lotId (Integer), totalQty (Long)}.
     */
    @Query(value = """
            SELECT i.lot_id, SUM(i.quantity) AS total_qty
            FROM inventory i
            WHERE i.lot_id = :lotId
            GROUP BY i.lot_id
            """, nativeQuery = true)
    List<Object[]> sumStockByLotId(@Param("lotId") Integer lotId);

    /**
     * Tổng tồn kho theo lô — batch cho tất cả lô có hàng.
     * Trả về Object[]{lotId (Integer), totalQty (Long)}.
     */
    @Query(value = """
            SELECT i.lot_id, SUM(i.quantity) AS total_qty
            FROM inventory i
            WHERE i.quantity > 0
            GROUP BY i.lot_id
            """, nativeQuery = true)
    List<Object[]> sumStockByLot();
}
    