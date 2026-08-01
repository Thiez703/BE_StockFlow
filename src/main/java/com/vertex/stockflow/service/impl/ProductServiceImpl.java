package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.StatusEnum;
import com.vertex.stockflow.dto.request.ProductRequest;
import com.vertex.stockflow.dto.response.ProductResponse;
import com.vertex.stockflow.entity.CategoryEntity;
import com.vertex.stockflow.entity.ProductEntity;
import com.vertex.stockflow.entity.UnitEntity;
import com.vertex.stockflow.exception.DuplicateResourceException;
import com.vertex.stockflow.exception.IllegalOperationException;
import com.vertex.stockflow.exception.ResourceNotFoundException;
import com.vertex.stockflow.mapper.ProductMapper;
import com.vertex.stockflow.repository.CategoryRepository;
import com.vertex.stockflow.repository.ProductRepository;
import com.vertex.stockflow.repository.ProductUnitRepository;
import com.vertex.stockflow.repository.UnitRepository;
import com.vertex.stockflow.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UnitRepository unitRepository;
    private final ProductUnitRepository productUnitRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Mã sản phẩm đã tồn tại");
        }

        CategoryEntity category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalOperationException("Category không tồn tại"));
        }

        UnitEntity baseUnit = unitRepository.findById(request.getBaseUnitId())
                .orElseThrow(() -> new IllegalOperationException("Đơn vị cơ sở không tồn tại"));

        ProductEntity entity = ProductEntity.builder()
                .code(request.getCode())
                .name(request.getName())
                .category(category)
                .baseUnit(baseUnit)
                .minStock(request.getMinStock())
                .status(request.getStatus() != null ? request.getStatus() : StatusEnum.ACTIVE)
                .build();

        return productMapper.toResponse(productRepository.save(entity));
    }

    @Override
    public ProductResponse update(Integer id, ProductRequest request) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));

        if (productRepository.existsByCodeAndIdNot(request.getCode(), id)) {
            throw new DuplicateResourceException("Mã sản phẩm đã tồn tại");
        }

        CategoryEntity category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalOperationException("Category không tồn tại"));
        }

        UnitEntity baseUnit = unitRepository.findById(request.getBaseUnitId())
                .orElseThrow(() -> new IllegalOperationException("Đơn vị cơ sở không tồn tại"));

        entity.setCode(request.getCode());
        entity.setName(request.getName());
        entity.setCategory(category);
        entity.setBaseUnit(baseUnit);
        entity.setMinStock(request.getMinStock());
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }

        return productMapper.toResponse(productRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));

        if (productUnitRepository.existsByProductId(id)) {
            throw new IllegalOperationException("Không thể xóa: sản phẩm còn đơn vị quy đổi đang sử dụng");
        }

        productRepository.delete(entity);
    }

    @Override
    public ProductResponse getById(Integer id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));
        return productMapper.toResponse(entity);
    }

    @Override
    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .toList();
    }
}
