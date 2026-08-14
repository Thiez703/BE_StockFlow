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
                // Ràng buộc MỚI: 1 vị trí chỉ được có tối đa 1 dòng inventory
                // => hiện thực hoá luật "1 vị trí = 1 lô" ở tầng DB, không phụ thuộc code.
                // Nhờ nó mà mỗi ô trên sơ đồ luôn chỉ có đúng 1 lô để hiển thị.
                @UniqueConstraint(
                        name = "uk_inventory_location",
                        columnNames = {"location_id"}
                )
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
