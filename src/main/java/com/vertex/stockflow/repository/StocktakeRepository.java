package com.vertex.stockflow.repository;

import com.vertex.stockflow.common.enums.ApprovalStatusEnum;
import com.vertex.stockflow.entity.StocktakeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StocktakeRepository extends JpaRepository<StocktakeEntity, Integer> {
    boolean existsByCode(String code);

    Page<StocktakeEntity> findByWarehouseId(Integer warehouseId, Pageable pageable);
    
    Page<StocktakeEntity> findByWarehouseIdAndCreatedById(Integer warehouseId, Integer createdById, Pageable pageable);

    List<StocktakeEntity> findByWarehouseIdAndStatus(Integer warehouseId, ApprovalStatusEnum status);
}
