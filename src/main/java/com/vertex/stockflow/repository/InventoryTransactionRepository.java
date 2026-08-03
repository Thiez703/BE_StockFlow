package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.InventoryTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransactionEntity, Integer> {
}