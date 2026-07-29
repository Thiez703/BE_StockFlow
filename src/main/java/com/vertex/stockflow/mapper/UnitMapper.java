package com.vertex.stockflow.mapper;

import com.vertex.stockflow.dto.response.UnitResponse;
import com.vertex.stockflow.entity.UnitEntity;
import org.springframework.stereotype.Component;

@Component
public class UnitMapper {
    public UnitResponse toResponse(UnitEntity unitEntity){
        return UnitResponse.builder()
            .id(unitEntity.getId())
            .code(unitEntity.getCode())
            .name(unitEntity.getName())
            .status(unitEntity.getStatus())
            .build();
    }
}
