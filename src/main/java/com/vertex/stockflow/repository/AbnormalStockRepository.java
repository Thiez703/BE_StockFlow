package com.vertex.stockflow.repository;

import com.vertex.stockflow.common.enums.ApprovalStatusEnum;
import com.vertex.stockflow.entity.AbnormalStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AbnormalStockRepository extends JpaRepository<AbnormalStockEntity, Integer> {
    boolean existsByCode(String code);

    List<AbnormalStockEntity> findByWarehouseId(Integer warehouseId);

    List<AbnormalStockEntity> findByWarehouseIdAndStatus(Integer warehouseId, ApprovalStatusEnum status);
}
