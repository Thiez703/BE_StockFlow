package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
    // Dùng để kiểm tra Category còn Product tham chiếu trước khi cho phép xóa.
    boolean existsByCategoryId(Integer categoryId);
}
