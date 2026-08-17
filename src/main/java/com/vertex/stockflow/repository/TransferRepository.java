package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.TransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransferRepository extends JpaRepository<TransferEntity, Integer>, JpaSpecificationExecutor<TransferEntity> {
}
