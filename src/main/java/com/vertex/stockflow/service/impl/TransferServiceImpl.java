package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.DocumentStatusEnum;
import com.vertex.stockflow.common.enums.RefTypeEnum;
import com.vertex.stockflow.dto.request.TransferCreateRequest;
import com.vertex.stockflow.dto.request.TransferDetailInput;
import com.vertex.stockflow.dto.response.TransferDetailResponse;
import com.vertex.stockflow.dto.response.TransferResponse;
import com.vertex.stockflow.entity.*;
import com.vertex.stockflow.exception.IllegalOperationException;
import com.vertex.stockflow.exception.ResourceNotFoundException;
import com.vertex.stockflow.repository.*;
import com.vertex.stockflow.service.InventoryService;
import com.vertex.stockflow.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final TransferDetailRepository transferDetailRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final LotRepository lotRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;

    @Override
    @Transactional
    public TransferResponse create(TransferCreateRequest request, User actor) {

        WarehouseEntity warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kho"));

        UserEntity createdBy = findUserOrThrow(actor);

        TransferEntity transfer = TransferEntity.builder()
                .code("TEMP-" + java.util.UUID.randomUUID().toString().substring(0, 8))
                .warehouse(warehouse)
                .createdBy(createdBy)
                .status(DocumentStatusEnum.POSTED)
                .note(request.getNote())
                .build();
        transfer = transferRepository.save(transfer);
        transfer.setCode("DC-" + String.format("%06d", transfer.getId()));

        List<TransferDetailEntity> details = new ArrayList<>();

        for (TransferDetailInput input : request.getDetails()) {

            if (input.getFromLocationId().equals(input.getToLocationId())) {
                throw new IllegalOperationException("Vị trí nguồn và vị trí đích không được trùng nhau");
            }

            ProductEntity product = productRepository.findById(input.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));

            LotEntity lot = lotRepository.findById(input.getLotId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lô hàng"));

            StorageLocationEntity fromLocation = storageLocationRepository.findById(input.getFromLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vị trí nguồn"));

            StorageLocationEntity toLocation = storageLocationRepository.findById(input.getToLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vị trí đích"));

            // Validate cùng warehouse
            if (!fromLocation.getWarehouse().getId().equals(warehouse.getId())
                    || !toLocation.getWarehouse().getId().equals(warehouse.getId())) {
                throw new IllegalOperationException("Hai vị trí phải cùng thuộc kho " + warehouse.getCode());
            }

            // Giảm tồn tại vị trí nguồn (sẽ validate đủ quantity bên trong)
            inventoryService.updateInventory(
                    warehouse.getId(), product.getId(), lot.getId(), fromLocation.getId(),
                    -input.getQuantity(), RefTypeEnum.TRANSFER, transfer.getId(), createdBy.getId());

            // Cộng tồn tại vị trí đích (sẽ validate capacity bên trong)
            inventoryService.updateInventory(
                    warehouse.getId(), product.getId(), lot.getId(), toLocation.getId(),
                    input.getQuantity(), RefTypeEnum.TRANSFER, transfer.getId(), createdBy.getId());

            details.add(TransferDetailEntity.builder()
                    .transfer(transfer)
                    .product(product)
                    .lot(lot)
                    .fromLocation(fromLocation)
                    .toLocation(toLocation)
                    .quantity(input.getQuantity())
                    .build());
        }

        transferDetailRepository.saveAll(details);
        transfer = transferRepository.save(transfer);

        return toResponse(transfer, details);
    }

    @Override
    @Transactional(readOnly = true)
    public TransferResponse getById(Integer id) {
        TransferEntity transfer = transferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu điều chuyển"));
        List<TransferDetailEntity> details = transferDetailRepository.findByTransferId(id);
        return toResponse(transfer, details);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransferResponse> search(Integer warehouseId, Pageable pageable) {
        Page<TransferEntity> page;
        if (warehouseId != null) {
            page = transferRepository.findAll(
                    (root, query, cb) -> cb.equal(root.get("warehouse").get("id"), warehouseId),
                    pageable);
        } else {
            page = transferRepository.findAll(pageable);
        }
        return page.map(t -> {
            List<TransferDetailEntity> details = transferDetailRepository.findByTransferId(t.getId());
            return toResponse(t, details);
        });
    }

    private TransferResponse toResponse(TransferEntity entity, List<TransferDetailEntity> details) {
        return TransferResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .warehouseId(entity.getWarehouse().getId())
                .status(entity.getStatus())
                .voidReason(entity.getVoidReason())
                .note(entity.getNote())
                .createdByName(entity.getCreatedBy().getFullName())
                .createdAt(entity.getCreatedAt())
                .details(details.stream().map(this::toDetailResponse).toList())
                .build();
    }

    private TransferDetailResponse toDetailResponse(TransferDetailEntity d) {
        return TransferDetailResponse.builder()
                .id(d.getId())
                .productId(d.getProduct().getId())
                .productCode(d.getProduct().getCode())
                .productName(d.getProduct().getName())
                .unit(d.getProduct().getUnit())
                .lotId(d.getLot().getId())
                .lotCode(d.getLot().getLotCode())
                .fromLocationId(d.getFromLocation().getId())
                .fromLocationCode(d.getFromLocation().getLocationCode())
                .toLocationId(d.getToLocation().getId())
                .toLocationCode(d.getToLocation().getLocationCode())
                .quantity(d.getQuantity())
                .build();
    }

    private UserEntity findUserOrThrow(User actor) {
        return userRepository.findByEmail(actor.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy thông tin người dùng. Phiên đăng nhập có thể đã hết hạn."));
    }
}
