package com.vertex.stockflow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "product_units",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_product_units_product_unit", columnNames = {"product_id", "unit_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUnitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private UnitEntity unit;

    @Column(name = "conversion_rate", nullable = false)
    private Integer conversionRate;
}
