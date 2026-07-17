package com.vertex.stockflow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "storage_locations")
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

    @Column(name = "zone_code", length = 20)
    private String zoneCode;

    @Column(name = "location_code", nullable = false, length = 20)
    private String locationCode;
}
