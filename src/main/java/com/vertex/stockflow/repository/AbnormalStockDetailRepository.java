package com.vertex.stockflow.repository;

import com.vertex.stockflow.common.enums.ApprovalStatusEnum;
import com.vertex.stockflow.entity.AbnormalStockDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AbnormalStockDetailRepository extends JpaRepository<AbnormalStockDetailEntity, Integer> {
    List<AbnormalStockDetailEntity> findByAbnormalStockId(Integer abnormalStockId);

    // Lấy chi tiết của NHIỀU phiếu trong 1 query duy nhất - dùng ở getByWarehouseId() để tránh
    // N+1 (thay vì gọi findByAbnormalStockId riêng cho từng phiếu trong vòng lặp).
    List<AbnormalStockDetailEntity> findByAbnormalStock_IdIn(List<Integer> abnormalStockIds);

    // Hạ tầng cho quyết định #5 (chặn phiếu trùng lặp trên cùng lô, ngoài SRS):
    // dùng để StocktakeService kiểm tra chéo trước khi tạo phiếu kiểm kê cho 1 lô.
    boolean existsByLot_IdAndAbnormalStock_Status(Integer lotId, ApprovalStatusEnum status);
}
