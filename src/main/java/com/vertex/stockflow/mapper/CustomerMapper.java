package com.vertex.stockflow.mapper;

import com.vertex.stockflow.dto.request.CustomerRequest;
import com.vertex.stockflow.dto.response.CustomerReponse;
import com.vertex.stockflow.entity.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    public CustomerEntity toEntity(CustomerRequest request){
        return CustomerEntity.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();
    }
    public void updateEntity(CustomerEntity entity, CustomerRequest request){
        entity.setName(request.getName());
        entity.setPhone(request.getPhone());
        entity.setAddress(request.getAddress());
    }
    public CustomerReponse toReponse(CustomerEntity entity){
        return CustomerReponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .phone(entity.getPhone())
                .address(entity.getAddress())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

}
