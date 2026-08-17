package com.vertex.stockflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "inventory",
        uniqueConstraints = {
                // Ràng buộc cũ: chặn 2 dòng y hệt nhau (cùng kho, cùng SP, cùng lô, cùng vị trí)
                @UniqueConstraint(
                        name = "uk_inventory_warehouse_product_lot_location",
                        columnNames = {"warehouse_id", "product_id", "lot_id", "location_id"}
                ),
                // Đã bỏ uk_inventory_location — 1 vị trí giờ được chứa nhiều lô/sản phẩm,
                // giới hạn bằng capacity của StorageLocationEntity thay vì cứng 1 lô.
                // Chạy thủ công trên DB: ALTER TABLE inventory DROP INDEX uk_inventory_location;
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private WarehouseEntity warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", nullable = false)
    private LotEntity lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = true)
    private StorageLocationEntity location;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 0;

    @Version
    @Column(nullable = false)
    @Builder.Default
    private Integer version = 0;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
