package com.vertex.stockflow.entity;

import com.vertex.stockflow.common.enums.ApprovalStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "abnormal_stocks",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_abnormal_stocks_code", columnNames = "code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AbnormalStockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private WarehouseEntity warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private UserEntity createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private UserEntity approvedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ApprovalStatusEnum status = ApprovalStatusEnum.PENDING;

    @Column(name = "reject_reason", length = 255)
    private String rejectReason;

    // Optimistic lock cấp phiếu: chống 2 người duyệt/từ chối cùng lúc trên 1 phiếu
    // (không có trong SRS gốc, bổ sung theo cùng nguyên tắc BR-13 đang áp dụng cho InventoryEntity,
    // và đồng bộ với version vừa thêm vào StocktakeEntity).
    @Version
    @Column(nullable = false)
    @Builder.Default
    private Integer version = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
