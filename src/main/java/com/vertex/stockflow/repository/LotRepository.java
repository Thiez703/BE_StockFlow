package com.vertex.stockflow.repository;

import com.vertex.stockflow.common.enums.StatusEnum;
import com.vertex.stockflow.entity.LotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LotRepository extends JpaRepository<LotEntity, Integer> {
    boolean existsByProductIdAndLotCode(Integer productId, String lotCode);
    boolean existsByProductIdAndLotCodeAndIdNot(Integer productId, String lotCode, Integer id);
    List<LotEntity> findByProductId(Integer productId);
    List<LotEntity>findByStatusAndExpDateBefore(StatusEnum status, LocalDate date);
}
