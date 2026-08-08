package com.vertex.stockflow.repository;

import com.vertex.stockflow.common.enums.StatusEnum;
import com.vertex.stockflow.entity.LotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LotRepository extends JpaRepository<LotEntity, Integer> {
    boolean existsByProductIdAndLotCode(Integer productId, String lotCode);
    List<LotEntity> findByProductId(Integer productId);
    List<LotEntity> findByStatusAndExpDateBefore(StatusEnum status, LocalDate date);

    @Query("""
        SELECT l FROM LotEntity l
        WHERE l.product.id = :productId
          AND EXISTS (SELECT i FROM InventoryEntity i WHERE i.lot = l AND i.quantity > 0)
        ORDER BY l.expDate ASC
        """)
    List<LotEntity> findAvailableLotsByProductOrderByExpDateAsc(@Param("productId") Integer productId);
}