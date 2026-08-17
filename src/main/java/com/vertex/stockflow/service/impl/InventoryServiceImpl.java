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
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin tài khoản thao tác (ID: " + createdByUserId + "). Vui lòng thử đăng nhập lại."));

            WarehouseEntity warehouse = warehouseRepository.findById(warehouseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kho hàng (ID: " + warehouseId + "). Kho này có thể đã bị xóa hoặc ẩn."));

            ProductEntity product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm (ID: " + productId + "). Sản phẩm này có thể không tồn tại trong hệ thống."));

            LotEntity lot = lotRepository.findById(lotId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lô hàng (ID: " + lotId + "). Vui lòng kiểm tra lại thông tin lô."));

            StorageLocationEntity location = storageLocationRepository.findById(locationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vị trí lưu trữ (ID: " + locationId + "). Vui lòng chọn một vị trí hợp lệ."));

            // Dọn dẹp dòng inventory rác (quantity = 0) còn giữ location
            for (InventoryEntity existing : inventoryRepository.findAllByLocationId(locationId)) {
                if (existing.getQuantity() == 0
                        && (!existing.getProduct().getId().equals(productId) || !existing.getLot().getId().equals(lotId))) {
                    existing.setLocation(null);
                    inventoryRepository.saveAndFlush(existing);
                }
            }

            // Kiểm tra sức chứa vị trí (nếu có cấu hình capacity)
            if (location.getCapacity() != null && quantityDelta > 0) {
                int used = inventoryRepository.sumQuantityByLocationId(locationId);
                int afterAdd = used + quantityDelta;
                if (afterAdd > location.getCapacity()) {
                    throw new IllegalOperationException(
                            "Vị trí " + location.getLocationCode() + " vượt sức chứa (capacity: "
                                    + location.getCapacity() + ", hiện có: " + used
                                    + ", đang thêm: " + quantityDelta + ")");
                }
            }

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
                throw new IllegalOperationException("Số lượng tồn kho không đủ để thực hiện xuất kho. Hiện tại chỉ còn " + inventory.getQuantity() + " sản phẩm trong hệ thống.");
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

    @Override
    @Transactional(readOnly = true)
    public Page<com.vertex.stockflow.dto.response.InventoryByProductResponse> searchByProduct(Integer productId, Pageable pageable) {
        return inventoryRepository.findInventoryByProduct(productId, pageable);
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
