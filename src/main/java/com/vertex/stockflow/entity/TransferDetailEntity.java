package com.vertex.stockflow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transfer_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_id", nullable = false)
    private TransferEntity transfer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", nullable = false)
    private LotEntity lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_location_id", nullable = false)
    private StorageLocationEntity fromLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_location_id", nullable = false)
    private StorageLocationEntity toLocation;

    @Column(nullable = false)
    private Integer quantity;
}
