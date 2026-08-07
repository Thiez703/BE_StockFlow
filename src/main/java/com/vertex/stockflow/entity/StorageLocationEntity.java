package com.vertex.stockflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "storage_locations",
        uniqueConstraints = {
                // Ràng buộc cũ: trong 1 kho, mã vị trí không được trùng
                @UniqueConstraint(
                        name = "uk_storage_locations_warehouse_location",
                        columnNames = {"warehouse_id", "location_code"}
                ),
                // Ràng buộc MỚI: trong 1 kho, không có 2 vị trí cùng đứng ở 1 ô lưới.
                // Thiếu cái này thì 2 bản ghi cùng là (A, 1) -> sơ đồ vẽ đè lên nhau.
                @UniqueConstraint(
                        name = "uk_storage_locations_wh_row_col",
                        columnNames = {"warehouse_id", "row_label", "col_index"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StorageLocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private WarehouseEntity warehouse;

    @Column(name = "row_label", nullable = false, length = 5)
    private String rowLabel;       // nhãn hàng: "A", "B", ... "F" -> FE gom ô theo giá trị này

    @Column(name = "col_index", nullable = false)
    private Integer colIndex;      // số thứ tự cột trong hàng: 1..6 -> FE dùng để sắp thứ tự ô

    @Column(name = "location_code", nullable = false, length = 20)
    private String locationCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
