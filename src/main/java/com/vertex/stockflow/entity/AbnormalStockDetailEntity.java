package com.vertex.stockflow.entity;

import com.vertex.stockflow.common.enums.AbnormalReasonEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "abnormal_stock_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AbnormalStockDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "abnormal_stock_id", nullable = false)
    private AbnormalStockEntity abnormalStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", nullable = false)
    private LotEntity lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private StorageLocationEntity location;

    // Lưu dương; khi áp dụng vào tồn kho lúc duyệt LUÔN được diễn giải là số cần trừ
    // (cả 4 lý do DAMAGED/LOST/EXPIRED/OTHER đều là hao hụt, không có chiều tăng tồn).
    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason_type", nullable = false, length = 20)
    private AbnormalReasonEnum reasonType;

    // Bắt buộc khi reasonType = OTHER (validate ở StocktakeService/AbnormalStockServiceImpl, không ở entity).
    @Column(length = 255)
    private String note;
}
