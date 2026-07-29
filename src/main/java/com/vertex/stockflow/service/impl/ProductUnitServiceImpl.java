package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.StatusEnum;
import com.vertex.stockflow.dto.request.ProductUnitRequest;
import com.vertex.stockflow.dto.response.ProductUnitResponse;
import com.vertex.stockflow.entity.ProductEntity;
import com.vertex.stockflow.entity.ProductUnitEntity;
import com.vertex.stockflow.entity.UnitEntity;
import com.vertex.stockflow.exception.IllegalOperationException;
import com.vertex.stockflow.exception.ResourceNotFoundException;
import com.vertex.stockflow.mapper.ProductUnitMapper;
import com.vertex.stockflow.repository.ProductRepository;
import com.vertex.stockflow.repository.ProductUnitRepository;
import com.vertex.stockflow.repository.UnitRepository;
import com.vertex.stockflow.service.ProductUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductUnitServiceImpl implements ProductUnitService {

    private final ProductRepository productRepository;
    private final ProductUnitRepository productUnitRepository;
    private final ProductUnitMapper productUnitMapper;
    private final UnitRepository unitRepository;


    @Override
    public ProductUnitResponse create(Integer productId, ProductUnitRequest request) {

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm với id " + productId + " không tồn tại"));

        UnitEntity unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("Đơn vị với id " + request.getUnitId() + " không tồn tại"));

        if (unit.getStatus() != StatusEnum.ACTIVE) {
            throw new IllegalOperationException("Đơn vị với id " + request.getUnitId() + " chưa được kích hoạt");
        }

        if (unit.getId().equals(product.getBaseUnit().getId())) {
            throw new IllegalOperationException("Không thể thêm đơn vị cơ sở làm đơn vị của sản phẩm");
        }
        if (productUnitRepository.existsByProductIdAndUnitId(productId, request.getUnitId())) {
            throw new IllegalOperationException("Sản phẩm với id " + productId + " đã có đơn vị với id " + request.getUnitId());
        }

        ProductUnitEntity productUnitEntity = ProductUnitEntity.builder()
                .product(product)
                .unit(unit)
                .conversionRate(request.getConversionRate())
                .build();

        return productUnitMapper.toResponse(productUnitRepository.save(productUnitEntity));
    }

    @Override
    public ProductUnitResponse update(Integer productId, Integer id, ProductUnitRequest request) {

        UnitEntity unitEntity = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("Đơn vị với id " + request.getUnitId() + " không tồn tại"));

        ProductUnitEntity productUnitEntity = productUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn vị của sản phẩm với id " + id + " không tồn tại"));

        if (!productUnitEntity.getProduct().getId().equals(productId)) {
            throw new IllegalOperationException("Đơn vị của sản phẩm với id " + id + " không thuộc sản phẩm với id " + productId);
        }

        if (unitEntity.getStatus() != StatusEnum.ACTIVE) {
            throw new IllegalOperationException("Đơn vị với id " + request.getUnitId() + " chưa được kích hoạt");
        }


        if (unitEntity.getId().equals(productUnitEntity.getProduct().getBaseUnit().getId())) {
            throw new IllegalOperationException("Không thể thêm đơn vị cơ sở làm đơn vị của sản phẩm");
        }

        if (productUnitRepository.existsByProductIdAndUnitIdAndIdNot(productId, request.getUnitId(), id )) {
            throw new IllegalOperationException("Sản phẩm với id " + productId + " đã có đơn vị với id " + request.getUnitId());
        }
        productUnitEntity.setUnit(unitEntity);
        productUnitEntity.setConversionRate(request.getConversionRate());

        return productUnitMapper.toResponse(productUnitRepository.save(productUnitEntity));
    }

    @Override
    public List<ProductUnitResponse> getByProductId(Integer productId) {

        return productUnitRepository.findByProductId(productId).stream()
                .map(productUnitMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Integer productId, Integer id) {

        ProductUnitEntity productUnitEntity = productUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn vị của sản phẩm với id " + id + " không tồn tại"));

        if (!productUnitEntity.getProduct().getId().equals(productId)) {
            throw new IllegalOperationException("Đơn vị của sản phẩm với id " + id + " không thuộc sản phẩm với id " + productId);
        }

        productUnitRepository.delete(productUnitEntity);
    }
}
