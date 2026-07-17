package com.vertex.stockflow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stocktake_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StocktakeDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stocktake_id", nullable = false)
    private StocktakeEntity stocktake;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", nullable = false)
    private LotEntity lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private StorageLocationEntity location;

    @Column(name = "system_qty", nullable = false)
    private Integer systemQty;

    @Column(name = "actual_qty", nullable = false)
    private Integer actualQty;

    @Column(name = "diff_qty", nullable = false)
    private Integer diffQty;
}
