package com.vertex.stockflow.mapper;

import com.vertex.stockflow.common.enums.StatusEnum;
import com.vertex.stockflow.dto.response.LotResponse;
import com.vertex.stockflow.entity.LotEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class LotMapper {

    private LotMapper() {
    }

    public static LotResponse toResponse(LotEntity entity) {
        return LotResponse.builder()
                .id(entity.getId())
                .productId(entity.getProduct().getId())
                .productName(entity.getProduct().getName())
                .lotCode(entity.getLotCode())
                .status(entity.getExpDate().isBefore(LocalDate.now()) ? StatusEnum.EXPIRED : StatusEnum.ACTIVE)
                .mfgDate(entity.getMfgDate())
                .expDate(entity.getExpDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
