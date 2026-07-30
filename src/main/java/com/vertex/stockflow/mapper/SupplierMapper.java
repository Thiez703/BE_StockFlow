package com.vertex.stockflow.mapper;

import com.vertex.stockflow.dto.response.SupplierResponse;
import com.vertex.stockflow.entity.SupplierEntity;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {
    public SupplierResponse toResponse(SupplierEntity entity) {
        return SupplierResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .taxCode(entity.getTaxCode())
                .contactPerson(entity.getContactPerson())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .address(entity.getAddress())
                .note(entity.getNote())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
