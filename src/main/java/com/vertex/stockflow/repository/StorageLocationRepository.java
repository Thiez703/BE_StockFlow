package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.StorageLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StorageLocationRepository extends JpaRepository<StorageLocationEntity, Integer> {
    List<StorageLocationEntity> findByWarehouseId(Integer warehouseId);
    boolean existsByWarehouseIdAndLocationCode(Integer warehouseId, String locationCode);
    boolean existsByWarehouseIdAndLocationCodeAndIdNot(Integer warehouseId, String locationCode, Integer id);
    void deleteByWarehouseId(Integer warehouseId);
}
