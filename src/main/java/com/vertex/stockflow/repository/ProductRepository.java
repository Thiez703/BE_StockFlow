package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
    // Dùng để kiểm tra Category còn Product tham chiếu trước khi cho phép xóa.
    boolean existsByCategoryId(Integer categoryId);

    // Check trùng code khi TẠO MỚI (FR-07 - code UNIQUE).
    boolean existsByCode(String code);

    // Check trùng code khi CẬP NHẬT - loại trừ chính sản phẩm đang sửa.
    boolean existsByCodeAndIdNot(String code, Integer id);
}
