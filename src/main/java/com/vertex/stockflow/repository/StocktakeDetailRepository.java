package com.vertex.stockflow.repository;

import com.vertex.stockflow.common.enums.ApprovalStatusEnum;
import com.vertex.stockflow.entity.StocktakeDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StocktakeDetailRepository extends JpaRepository<StocktakeDetailEntity, Integer> {
    List<StocktakeDetailEntity> findByStocktakeId(Integer stocktakeId);

    // Lấy chi tiết của NHIỀU phiếu trong 1 query duy nhất - dùng ở getByWarehouseId() để tránh
    // N+1 (thay vì gọi findByStocktakeId riêng cho từng phiếu trong vòng lặp).
    List<StocktakeDetailEntity> findByStocktake_IdIn(List<Integer> stocktakeIds);

    boolean existsByLot_IdAndStocktake_Status(Integer lotId, ApprovalStatusEnum status);
}
