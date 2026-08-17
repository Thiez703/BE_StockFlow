package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.TransferDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferDetailRepository extends JpaRepository<TransferDetailEntity, Integer> {
    List<TransferDetailEntity> findByTransferId(Integer transferId);
}
