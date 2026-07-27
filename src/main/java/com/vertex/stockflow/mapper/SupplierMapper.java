package com.vertex.stockflow.mapper;

import com.vertex.stockflow.dto.request.SupplierRequest;
import com.vertex.stockflow.dto.response.SupplierReponse;
import com.vertex.stockflow.entity.SupplierEntity;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {
    public SupplierEntity toEntity(SupplierRequest request, String code){
        return SupplierEntity.builder()
                .code(code)
                .name(request.getName())
                .taxCode(request.getTaxCode())
                .contactPerson(request.getContactPerson())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .note(request.getNote())
                .build();
    }
    public void updateEntity(SupplierEntity entity, SupplierRequest request){
        entity.setName(request.getName());
        entity.setTaxCode(request.getTaxCode());
        entity.setContactPerson(request.getContactPerson());
        entity.setPhone(request.getPhone());
        entity.setEmail(request.getEmail());
        entity.setAddress(request.getAddress());
        entity.setNote(request.getNote());
    }
    public SupplierReponse toReponse(SupplierEntity entity){
        return SupplierReponse.builder()
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
