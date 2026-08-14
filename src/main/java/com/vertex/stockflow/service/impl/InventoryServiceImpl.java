package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.RefTypeEnum;
import com.vertex.stockflow.common.specification.InventorySpecification;
import com.vertex.stockflow.dto.response.InventoryResponse;
import com.vertex.stockflow.entity.*;
import com.vertex.stockflow.exception.IllegalOperationException;
import com.vertex.stockflow.exception.ResourceNotFoundException;
import com.vertex.stockflow.repository.*;
import com.vertex.stockflow.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final LotRepository lotRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void updateInventory(Integer warehouseId, Integer productId, Integer lotId, Integer locationId,
                                Integer quantityDelta, RefTypeEnum refType, Integer refId, Integer createdByUserId) {

            UserEntity createdBy = userRepository.findById(createdByUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

            WarehouseEntity warehouse = warehouseRepository.findById(warehouseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kho"));

            ProductEntity product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));

            LotEntity lot = lotRepository.findById(lotId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lô hàng"));

            StorageLocationEntity location = storageLocationRepository.findById(locationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vị trí lưu trữ"));

            InventoryEntity inventory = inventoryRepository
                    .findByWarehouseIdAndProductIdAndLotIdAndLocationId(warehouseId, productId, lotId, locationId)
                    .orElseGet(() -> InventoryEntity.builder()
                            .warehouse(warehouse)
                            .product(product)
                            .lot(lot)
                            .location(location)
                            .quantity(0)
                            .build());

            int newQuantity = inventory.getQuantity() + quantityDelta;
            if (newQuantity < 0) {
                throw new IllegalOperationException("Số lượng tồn kho không đủ");
            }

            inventory.setQuantity(newQuantity);
            if (newQuantity == 0) {
                inventory.setLocation(null);
            }
            
            inventory = inventoryRepository.saveAndFlush(inventory);

            InventoryTransactionEntity transaction = InventoryTransactionEntity.builder()
                    .inventory(inventory)
                    .refType(refType)
                    .refId(refId)
                    .quantityChange(quantityDelta)
                    .balanceAfter(newQuantity)
                    .createdBy(createdBy)
                    .build();

            inventoryTransactionRepository.save(transaction);
        }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryResponse> search(Integer productId, Integer lotId, Integer locationId, Pageable pageable) {
        return inventoryRepository
                .findAll(InventorySpecification.filter(productId, lotId, locationId), pageable)
                .map(this::toResponse);
    }

    private InventoryResponse toResponse(InventoryEntity entity) {
        return InventoryResponse.builder()
                .id(entity.getId())
                .warehouseId(entity.getWarehouse().getId())
                .warehouseCode(entity.getWarehouse().getCode())
                .productId(entity.getProduct().getId())
                .productCode(entity.getProduct().getCode())
                .productName(entity.getProduct().getName())
                .lotId(entity.getLot().getId())
                .lotCode(entity.getLot().getLotCode())
                .locationId(entity.getLocation() != null ? entity.getLocation().getId() : null)
                .locationCode(entity.getLocation() != null ? entity.getLocation().getLocationCode() : null)
                .quantity(entity.getQuantity())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
