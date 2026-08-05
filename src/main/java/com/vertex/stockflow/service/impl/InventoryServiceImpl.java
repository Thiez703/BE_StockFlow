package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.RefTypeEnum;
import com.vertex.stockflow.entity.*;
import com.vertex.stockflow.exception.IllegalOperationException;
import com.vertex.stockflow.exception.ResourceNotFoundException;
import com.vertex.stockflow.repository.*;
import com.vertex.stockflow.service.InventoryService;
import lombok.RequiredArgsConstructor;
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

    @Override
    @Transactional
    public void updateInventory(Integer warehouseId, Integer productId, Integer lotId, Integer locationId,
                                Integer quantityDelta, RefTypeEnum refType, Integer refId) {

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
            inventoryRepository.save(inventory);

            InventoryTransactionEntity transaction = InventoryTransactionEntity.builder()
                    .inventory(inventory)
                    .refType(refType)
                    .refId(refId)
                    .quantityChange(quantityDelta)
                    .balanceAfter(newQuantity)
                    .build();

            inventoryTransactionRepository.save(transaction);
        }
    }
