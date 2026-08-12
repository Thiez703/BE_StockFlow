package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.AuditAction;
import com.vertex.stockflow.common.enums.DocumentStatusEnum;
import com.vertex.stockflow.common.enums.RefTypeEnum;
import com.vertex.stockflow.common.enums.RoleEnum;
import com.vertex.stockflow.common.enums.StatusEnum;
import com.vertex.stockflow.common.specification.InboundSpecification;
import com.vertex.stockflow.dto.request.InboundCreateRequest;
import com.vertex.stockflow.dto.request.InboundDetailInput;
import com.vertex.stockflow.dto.response.InboundResponse;
import com.vertex.stockflow.entity.*;
import com.vertex.stockflow.exception.IllegalOperationException;
import com.vertex.stockflow.exception.ResourceNotFoundException;
import com.vertex.stockflow.mapper.InboundMapper;
import com.vertex.stockflow.repository.*;
import com.vertex.stockflow.service.AuditLogService;
import com.vertex.stockflow.service.InboundService;
import com.vertex.stockflow.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InboundServiceImpl implements InboundService {

    private final InboundRepository inboundRepository;
    private final InboundDetailRepository inboundDetailRepository;
    private final WarehouseRepository warehouseRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final LotRepository lotRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public InboundResponse create(InboundCreateRequest request, User actor) {

        WarehouseEntity warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kho"));

        SupplierEntity supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà cung cấp"));

        UserEntity createdBy = findUserOrThrow(actor);

        // BR-01: POSTED ngay, không có DRAFT
        InboundEntity inbound = InboundEntity.builder()
                .code("TEMP")
                .supplier(supplier)
                .warehouse(warehouse)
                .createdBy(createdBy)
                .status(DocumentStatusEnum.POSTED)
                .note(request.getNote())
                .build();
        inbound = inboundRepository.save(inbound);
        inbound.setCode("PN-" + String.format("%06d", inbound.getId()));

        List<InboundDetailEntity> details = new ArrayList<>();

        for (InboundDetailInput input : request.getDetails()) {

            ProductEntity product = productRepository.findById(input.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));

            // Lot: chọn có sẵn hoặc tạo mới inline
            LotEntity lot = resolveLot(input, product);

            StorageLocationEntity location = storageLocationRepository.findById(input.getLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vị trí lưu trữ"));

            // FR-13: cập nhật tồn tức thời
            inventoryService.updateInventory(
                    warehouse.getId(), product.getId(), lot.getId(), location.getId(),
                    input.getQuantity(), RefTypeEnum.INBOUND, inbound.getId(), createdBy.getId());

            BigDecimal totalAmount = input.getUnitPrice()
                    .multiply(BigDecimal.valueOf(input.getQuantity()));

            details.add(InboundDetailEntity.builder()
                    .inbound(inbound)
                    .product(product)
                    .lot(lot)
                    .location(location)
                    .quantity(input.getQuantity())
                    .unitPrice(input.getUnitPrice())
                    .totalAmount(totalAmount)
                    .build());
        }

        details = inboundDetailRepository.saveAll(details);

        auditLogService.log(actor, AuditAction.INBOUND_CREATE, "inbounds", inbound.getId(),
                "Tạo phiếu nhập " + inbound.getCode());

        return InboundMapper.toResponse(inbound, details);
    }

    @Override
    @Transactional
    public void voidInbound(Integer id, String reason, User actor) {
        InboundEntity inbound = inboundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu nhập"));

        if (inbound.getStatus() == DocumentStatusEnum.VOIDED) {
            throw new IllegalOperationException("Phiếu đã bị hủy trước đó");
        }

        UserEntity voidedBy = findUserOrThrow(actor);

        // BR-04: người hủy phải khác người lập
        if (voidedBy.getId().equals(inbound.getCreatedBy().getId())) {
            throw new IllegalOperationException("Người hủy phải khác người lập phiếu");
        }
        // BR-19: chỉ ACCOUNTANT hoặc ADMIN được hủy phiếu
        if (voidedBy.getRole() != RoleEnum.ACCOUNTANT && voidedBy.getRole() != RoleEnum.ADMIN) {
            throw new IllegalOperationException("Chỉ ACCOUNTANT hoặc ADMIN được hủy phiếu");
        }

        // BR-02: sinh bút toán đảo — dùng INBOUND_VOID để phân biệt với giao dịch gốc trên thẻ kho
        List<InboundDetailEntity> details = inboundDetailRepository.findByInbound_IdIn(List.of(id));
        for (InboundDetailEntity detail : details) {
            inventoryService.updateInventory(
                    inbound.getWarehouse().getId(),
                    detail.getProduct().getId(),
                    detail.getLot().getId(),
                    detail.getLocation().getId(),
                    -detail.getQuantity(),
                    RefTypeEnum.INBOUND_VOID,
                    inbound.getId(),
                    voidedBy.getId());
        }

        inbound.setStatus(DocumentStatusEnum.VOIDED);
        inbound.setVoidReason(reason);
        inbound.setVoidedBy(voidedBy);
        inbound.setVoidedAt(LocalDateTime.now());
        inboundRepository.save(inbound);

        auditLogService.log(actor, AuditAction.INBOUND_VOID, "inbounds", inbound.getId(),
                "Hủy phiếu nhập " + inbound.getCode() + ": " + reason);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InboundResponse> search(Integer warehouseId, Integer supplierId,
                                         DocumentStatusEnum status,
                                         LocalDateTime from, LocalDateTime to,
                                         Pageable pageable) {

        Page<InboundEntity> page = inboundRepository.findAll(
                InboundSpecification.filter(warehouseId, supplierId, status, from, to),
                pageable);

        List<Integer> inboundIds = page.getContent().stream().map(InboundEntity::getId).toList();

        Map<Integer, List<InboundDetailEntity>> detailsByInboundId = inboundIds.isEmpty()
                ? Map.of()
                : inboundDetailRepository.findByInbound_IdIn(inboundIds)
                        .stream()
                        .collect(Collectors.groupingBy(d -> d.getInbound().getId()));

        return page.map(i -> InboundMapper.toResponse(i,
                detailsByInboundId.getOrDefault(i.getId(), List.of())));
    }

    @Override
    @Transactional(readOnly = true)
    public InboundResponse getById(Integer id) {
        InboundEntity inbound = inboundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu nhập"));
        List<InboundDetailEntity> details = inboundDetailRepository.findByInbound_IdIn(List.of(id));
        return InboundMapper.toResponse(inbound, details);
    }

    /**
     * Nếu lotId != null → tìm lô đã tồn tại.
     * Nếu lotId == null → tìm lô theo (productId, lotCode):
     *   - tồn tại → dùng lại (nhập thêm hàng vào lô cũ).
     *   - chưa có → tạo mới (bắt buộc expDate).
     */
    private LotEntity resolveLot(InboundDetailInput input, ProductEntity product) {
        if (input.getLotId() != null) {
            LotEntity lot = lotRepository.findById(input.getLotId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lô hàng"));
            if (!lot.getProduct().getId().equals(product.getId())) {
                throw new IllegalOperationException(
                        "Lô " + lot.getLotCode() + " không thuộc sản phẩm " + product.getCode());
            }
            return lot;
        }

        String lotCode = input.getLotCode();
        if (lotCode == null || lotCode.trim().isEmpty()) {
            // Delay 1ms nếu cần để tránh trùng lặp nếu tạo nhiều lô cùng lúc cho cùng 1 sản phẩm
            try { Thread.sleep(1); } catch (InterruptedException ignored) {}
            lotCode = "LOT-" + product.getCode() + "-" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        }
        final String finalLotCode = lotCode;

        // Tìm lô có sẵn cùng product + lotCode
        return lotRepository.findByProductIdAndLotCode(product.getId(), finalLotCode)
                .orElseGet(() -> {
                    // Tạo lô mới — expDate bắt buộc
                    if (input.getExpDate() == null) {
                        throw new IllegalOperationException(
                                "Tạo lô mới bắt buộc phải có ngày hết hạn (expDate)");
                    }
                    return lotRepository.save(LotEntity.builder()
                            .product(product)
                            .lotCode(finalLotCode)
                            .mfgDate(input.getMfgDate())
                            .expDate(input.getExpDate())
                            .status(StatusEnum.ACTIVE)
                            .build());
                });
    }

    private UserEntity findUserOrThrow(User actor) {
        return userRepository.findByEmail(actor.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + actor.getUsername()));
    }
}
