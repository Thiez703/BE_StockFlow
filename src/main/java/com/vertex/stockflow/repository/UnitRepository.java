package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.UnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository cho Unit (đơn vị tính).
 * Dùng để kiểm tra baseUnitId gửi lên có tồn tại không (baseUnit là bắt buộc của sản phẩm).
 */
@Repository
public interface UnitRepository extends JpaRepository<UnitEntity, Integer> {
}