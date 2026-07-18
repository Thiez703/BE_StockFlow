package com.vertex.stockflow.mapper;

import com.vertex.stockflow.dto.request.ProductRequest;
import com.vertex.stockflow.dto.response.ProductResponse;
import com.vertex.stockflow.entity.CategoryEntity;
import com.vertex.stockflow.entity.ProductEntity;
import com.vertex.stockflow.entity.UnitEntity;
import org.springframework.stereotype.Component;

/**
 * Nơi CHUYỂN ĐỔI qua lại giữa Entity (dữ liệu DB) và DTO (dữ liệu API).
 * Gom hết logic đổi kiểu vào một chỗ để Service gọn và không lặp code.
 * @Component: để Spring quản lý, nhờ đó Service inject vào dùng được.
 */
@Component
public class ProductMapper {

    /**
     * ProductRequest -> ProductEntity MỚI (dùng khi TẠO).
     * Không set id/status/createdAt: id do DB tự sinh, status mặc định ACTIVE,
     * createdAt/updatedAt do Entity tự set trong @PrePersist.
     */
    public ProductEntity toEntity(ProductRequest request, CategoryEntity category, UnitEntity baseUnit) {
        return ProductEntity.builder()
                .barcode(request.getBarcode())
                .name(request.getName())
                .category(category)
                .baseUnit(baseUnit)
                .minStock(request.getMinStock())
                .maxStock(request.getMaxStock())
                .build();
    }

    /**
     * Cập nhật một ProductEntity ĐÃ CÓ từ dữ liệu request (dùng khi SỬA).
     * Ghi đè các trường được phép sửa; id và createdAt giữ nguyên, updatedAt tự cập nhật.
     */
    public void updateEntity(ProductEntity entity, ProductRequest request, CategoryEntity category, UnitEntity baseUnit) {
        entity.setBarcode(request.getBarcode());
        entity.setName(request.getName());
        entity.setCategory(category);
        entity.setBaseUnit(baseUnit);
        entity.setMinStock(request.getMinStock());
        entity.setMaxStock(request.getMaxStock());
    }

    /**
     * ProductEntity -> ProductResponse (trả về cho FE).
     * QUAN TRỌNG: category/baseUnit là quan hệ LAZY (chỉ nạp khi truy cập).
     * Hàm này PHẢI được gọi khi còn trong transaction (Service có @Transactional),
     * nếu không dòng category.getName() sẽ ném LazyInitializationException.
     */
    public ProductResponse toResponse(ProductEntity entity) {
        CategoryEntity category = entity.getCategory();
        UnitEntity baseUnit = entity.getBaseUnit();

        return ProductResponse.builder()
                .id(entity.getId())
                .barcode(entity.getBarcode())
                .name(entity.getName())
                .categoryId(category != null ? category.getId() : null)
                .categoryName(category != null ? category.getName() : null)
                .baseUnitId(baseUnit != null ? baseUnit.getId() : null)
                .baseUnitName(baseUnit != null ? baseUnit.getName() : null)
                .minStock(entity.getMinStock())
                .maxStock(entity.getMaxStock())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}