package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.OutboundEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboundRepository extends JpaRepository<OutboundEntity, Integer> {
    @EntityGraph(attributePaths = {"createdBy", "customer"})
    Page<OutboundEntity> findByWarehouseId(Integer warehouseId, Pageable pageable);
}