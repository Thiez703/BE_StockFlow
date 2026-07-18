package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.StatusEnum;
import com.vertex.stockflow.dto.request.ProductRequest;
import com.vertex.stockflow.dto.response.ProductResponse;
import com.vertex.stockflow.entity.CategoryEntity;
import com.vertex.stockflow.entity.ProductEntity;
import com.vertex.stockflow.entity.UnitEntity;
import com.vertex.stockflow.exception.BusinessException;
import com.vertex.stockflow.exception.ResourceNotFoundException;
import com.vertex.stockflow.mapper.ProductMapper;
import com.vertex.stockflow.repository.CategoryRepository;
import com.vertex.stockflow.repository.ProductRepository;
import com.vertex.stockflow.repository.UnitRepository;
import com.vertex.stockflow.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * NƠI CHỨA TOÀN BỘ LOGIC NGHIỆP VỤ của Product (trái tim của module).
 * Controller gọi xuống đây; đây gọi xuống Repository và Mapper.
 *
 * @Service                : Spring quản lý như một bean nghiệp vụ.
 * @RequiredArgsConstructor: Lombok tự sinh constructor cho các field "final"
 *                           -> Spring tự inject repository/mapper vào.
 * @Transactional          : mỗi method chạy trong 1 giao dịch DB; lỗi giữa chừng thì rollback,
 *                           đồng thời cho phép mapper truy cập quan hệ LAZY an toàn.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UnitRepository unitRepository;
    private final ProductMapper productMapper;

    // ---------------- TẠO MỚI ----------------
    @Override
    public ProductResponse create(ProductRequest request) {
        validateStockRange(request);                                       // 1) minStock <= maxStock
        if (productRepository.existsByBarcode(request.getBarcode())) {     // 2) chặn barcode trùng
            throw new BusinessException("Barcode đã tồn tại: " + request.getBarcode());
        }
        UnitEntity baseUnit = findUnitOrThrow(request.getBaseUnitId());        // 3) baseUnit phải tồn tại
        CategoryEntity category = findCategoryOrNull(request.getCategoryId()); // 4) category có thì phải tồn tại
        ProductEntity entity = productMapper.toEntity(request, category, baseUnit); // 5) DTO -> Entity
        ProductEntity saved = productRepository.save(entity);                        // 6) lưu DB
        return productMapper.toResponse(saved);                                      // 7) Entity -> DTO
    }

    // ---------------- DANH SÁCH (chỉ ACTIVE) ----------------
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAll() {
        return productRepository.findByStatus(StatusEnum.ACTIVE)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    // ---------------- CHI TIẾT THEO ID ----------------
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(Integer id) {
        ProductEntity entity = findProductOrThrow(id);
        return productMapper.toResponse(entity);
    }

    // ---------------- CẬP NHẬT ----------------
    @Override
    public ProductResponse update(Integer id, ProductRequest request) {
        validateStockRange(request);
        ProductEntity entity = findProductOrThrow(id);   // phải tồn tại mới sửa được
        // Cho giữ nguyên barcode của chính nó, chỉ báo lỗi nếu trùng với sản phẩm KHÁC
        if (productRepository.existsByBarcodeAndIdNot(request.getBarcode(), id)) {
            throw new BusinessException("Barcode đã tồn tại: " + request.getBarcode());
        }
        UnitEntity baseUnit = findUnitOrThrow(request.getBaseUnitId());
        CategoryEntity category = findCategoryOrNull(request.getCategoryId());
        productMapper.updateEntity(entity, request, category, baseUnit);
        ProductEntity saved = productRepository.save(entity);
        return productMapper.toResponse(saved);
    }

    // ---------------- XÓA MỀM ----------------
    @Override
    public void delete(Integer id) {
        ProductEntity entity = findProductOrThrow(id);
        // KHÔNG xóa hẳn record (Product bị nhiều bảng tham chiếu: tồn kho, nhập, xuất...).
        // Chỉ đổi trạng thái INACTIVE -> giữ lịch sử, không vỡ khóa ngoại.
        entity.setStatus(StatusEnum.INACTIVE);
        productRepository.save(entity);
    }

    // ================= HÀM PHỤ DÙNG CHUNG =================

    private void validateStockRange(ProductRequest request) {
        if (request.getMinStock() != null && request.getMaxStock() != null
                && request.getMinStock() > request.getMaxStock()) {
            throw new BusinessException("minStock không được lớn hơn maxStock");
        }
    }

    private ProductEntity findProductOrThrow(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với id = " + id));
    }

    private UnitEntity findUnitOrThrow(Integer unitId) {
        return unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn vị với id = " + unitId));
    }

    private CategoryEntity findCategoryOrNull(Integer categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với id = " + categoryId));
    }
}