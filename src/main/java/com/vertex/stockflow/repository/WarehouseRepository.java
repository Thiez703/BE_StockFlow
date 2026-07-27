package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<WarehouseEntity, Integer> {
    boolean existsByCode(String code);
}
