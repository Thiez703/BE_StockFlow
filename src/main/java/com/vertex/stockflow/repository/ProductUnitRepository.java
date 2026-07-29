package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.ProductUnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductUnitRepository extends JpaRepository<ProductUnitEntity, Integer> {
    List<ProductUnitEntity> findByProductId(Integer productId);

    boolean existsByProductIdAndUnitId(Integer productId, Integer unitId);

    boolean existsByProductIdAndUnitIdAndIdNot(Integer productId, Integer unitId, Integer id);
    // Kiểm tra trùng khi sửa ngoại trừ id đang được sửa
}
