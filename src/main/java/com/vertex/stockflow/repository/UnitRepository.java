package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.UnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitRepository extends JpaRepository<UnitEntity, Integer> {
    boolean existsByCode(String code);
}
