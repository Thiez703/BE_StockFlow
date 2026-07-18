package com.vertex.stockflow.repository;

import com.vertex.stockflow.common.enums.StatusEnum;
import com.vertex.stockflow.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Tầng truy cập dữ liệu cho bảng products.
 * Kế thừa JpaRepository là có sẵn save/findById/findAll/deleteById... Spring tự sinh SQL.
 * Các hàm dưới chỉ cần đặt tên đúng quy tắc "query method", Spring tự tạo truy vấn.
 */
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {

    // Dùng khi TẠO mới: chặn barcode trùng.
    boolean existsByBarcode(String barcode);

    // Dùng khi SỬA: chặn trùng với sản phẩm KHÁC nhưng cho giữ barcode của chính nó.
    boolean existsByBarcodeAndIdNot(String barcode, Integer id);

    // Danh sách theo trạng thái. Vì xóa mềm nên list chỉ lấy status = ACTIVE.
    List<ProductEntity> findByStatus(StatusEnum status);
}