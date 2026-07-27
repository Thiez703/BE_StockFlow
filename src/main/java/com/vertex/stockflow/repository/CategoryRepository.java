package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
    List<CategoryEntity> findByParentIsNull();
    List<CategoryEntity> findByParentId(Integer parentId);
    boolean existsByParentId(Integer parentId);

    // Check trùng tên trong cùng 1 cha khi TẠO MỚI (chưa có id để loại trừ chính nó).
    // Tách riêng 2 method vì "parent_id = null" trong SQL không bao giờ match qua toán tử "=",
    // phải dùng "parent_id IS NULL" (Spring Data tự sinh qua ParentIsNull).
    boolean existsByParentIdAndName(Integer parentId, String name);
    boolean existsByParentIsNullAndName(String name);

    // Check trùng tên khi CẬP NHẬT - phải loại trừ chính category đang sửa (IdNot),
    // nếu không category sẽ luôn bị coi là "trùng" với chính nó khi giữ nguyên tên.
    boolean existsByParentIdAndNameAndIdNot(Integer parentId, String name, Integer id);
    boolean existsByParentIsNullAndNameAndIdNot(String name, Integer id);
}
