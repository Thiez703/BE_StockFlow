package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.OutboundEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboundRepository extends JpaRepository<OutboundEntity, Integer> {
    List<OutboundEntity> findByWarehouseId(Integer warehouseId);
}