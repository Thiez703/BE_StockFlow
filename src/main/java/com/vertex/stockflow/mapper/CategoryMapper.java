package com.vertex.stockflow.mapper;

import com.vertex.stockflow.dto.response.CategoryResponse;
import com.vertex.stockflow.entity.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponse toResponse(CategoryEntity entity) {
        return CategoryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy() != null ? entity.getCreatedBy().getEmail() : null)
                .updatedBy(entity.getUpdatedBy() != null ? entity.getUpdatedBy().getEmail() : null)
                .build();
    }
}
