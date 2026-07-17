package com.vertex.stockflow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "units",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_units_code", columnNames = "code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 50)
    private String name;
}
