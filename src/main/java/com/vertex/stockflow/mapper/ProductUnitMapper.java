package com.vertex.stockflow.mapper;

import com.vertex.stockflow.dto.request.ProductUnitRequest;
import com.vertex.stockflow.dto.response.ProductUnitResponse;
import com.vertex.stockflow.entity.ProductUnitEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductUnitMapper {
    public ProductUnitResponse toResponse(ProductUnitEntity Entity) {
        ProductUnitResponse response = new ProductUnitResponse();
        response.setId(Entity.getId());
        response.setUnitId(Entity.getUnit().getId());
        response.setUnitCode(Entity.getUnit().getCode());
        response.setUnitName(Entity.getUnit().getName());
        response.setConversionRate(Entity.getConversionRate());
        return response;
    }
}
