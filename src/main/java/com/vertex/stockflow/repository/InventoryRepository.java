package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryEntity, Integer> {
    Optional<InventoryEntity> findByWarehouseIdAndProductIdAndLotIdAndLocationId(
            Integer warehouseId, Integer productId, Integer lotId, Integer locationId);
}
    