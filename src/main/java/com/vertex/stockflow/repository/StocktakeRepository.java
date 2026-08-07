package com.vertex.stockflow.repository;

import com.vertex.stockflow.common.enums.ApprovalStatusEnum;
import com.vertex.stockflow.entity.StocktakeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StocktakeRepository extends JpaRepository<StocktakeEntity, Integer> {
    boolean existsByCode(String code);

    List<StocktakeEntity> findByWarehouseId(Integer warehouseId);

    List<StocktakeEntity> findByWarehouseIdAndStatus(Integer warehouseId, ApprovalStatusEnum status);
}
