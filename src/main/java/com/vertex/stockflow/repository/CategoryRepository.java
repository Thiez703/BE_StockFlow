package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository cho Category.
 * Dùng để kiểm tra categoryId gửi lên có tồn tại thật không trước khi gán vào sản phẩm.
 */
@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
}