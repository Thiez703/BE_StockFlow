package com.vertex.stockflow.mapper;

import com.vertex.stockflow.dto.response.ProductResponse;
import com.vertex.stockflow.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductResponse toResponse(ProductEntity entity) {
        return ProductResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .baseUnitId(entity.getBaseUnit().getId())
                .minStock(entity.getMinStock())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
