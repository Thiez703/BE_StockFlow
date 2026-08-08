package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.DocumentStatusEnum;
import com.vertex.stockflow.common.enums.IssueTypeEnum;
import com.vertex.stockflow.common.enums.RefTypeEnum;
import com.vertex.stockflow.common.enums.RoleEnum;
import com.vertex.stockflow.dto.request.OutboundCreateRequest;
import com.vertex.stockflow.dto.request.OutboundDetailInput;
import com.vertex.stockflow.dto.response.OutboundResponse;
import com.vertex.stockflow.entity.*;
import com.vertex.stockflow.exception.IllegalOperationException;
import com.vertex.stockflow.exception.ResourceNotFoundException;
import com.vertex.stockflow.mapper.OutboundMapper;
import com.vertex.stockflow.repository.*;
import com.vertex.stockflow.service.InventoryService;
import com.vertex.stockflow.service.OutboundService;
import lombok.RequiredArgsConstructor;
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
public class OutboundServiceImpl implements OutboundService {

    private final OutboundRepository outboundRepository;
    private final OutboundDetailRepository outboundDetailRepository;
    private final WarehouseRepository warehouseRepository;
    private final CustomerRepository customerRepository;
    private final LotRepository lotRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;

    @Override
    @Transactional
    public OutboundResponse create(OutboundCreateRequest request, User actor) {

        WarehouseEntity warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kho"));

        UserEntity createdBy = findUserOrThrow(actor);

        // BR-16 (SRS 1.3.0): customer_id chỉ bắt buộc khi issue_type = RETAIL
        // (RETAIL đã gộp nghĩa cũ của WHOLESALE - xuất bán có khách hàng/đại lý)
        CustomerEntity customer = null;
        if (request.getIssueType() == IssueTypeEnum.RETAIL) {
            if (request.getCustomerId() == null) {
                throw new IllegalOperationException("Xuất bán bắt buộc phải chọn khách hàng");
            }
            customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng"));
        }

        OutboundEntity outbound = OutboundEntity.builder()
                .code("TEMP")
                .issueType(request.getIssueType())
                .customer(customer)
                .warehouse(warehouse)
                .createdBy(createdBy)
                .status(DocumentStatusEnum.POSTED)
                .note(request.getNote())
                .build();
        outbound = outboundRepository.save(outbound);
        outbound.setCode("PX-" + String.format("%06d", outbound.getId()));

        List<OutboundDetailEntity> details = new ArrayList<>();

        for (OutboundDetailInput input : request.getDetails()) {

            LotEntity lot = lotRepository.findById(input.getLotId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lô hàng"));
            ProductEntity product = lot.getProduct();

            StorageLocationEntity location = storageLocationRepository.findById(input.getLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vị trí lưu trữ"));

            List<LotEntity> suggestedLots =
                    lotRepository.findAvailableLotsByProductOrderByExpDateAsc(product.getId());
            if (!suggestedLots.isEmpty()) {
                LotEntity suggested = suggestedLots.get(0);
                boolean pickedDifferentLot = !suggested.getId().equals(lot.getId());
                if (pickedDifferentLot &&
                        (input.getOverrideReason() == null || input.getOverrideReason().isBlank())) {
                    throw new IllegalOperationException(
                            "Lô " + lot.getLotCode() + " không phải lô FEFO gợi ý (" + suggested.getLotCode() +
                                    ") — bắt buộc nhập lý do khi chọn lô khác");
                }
            }

            inventoryService.updateInventory(
                    warehouse.getId(), product.getId(), lot.getId(), location.getId(),
                    -input.getQuantity(), RefTypeEnum.OUTBOUND, outbound.getId(), createdBy.getId());

            BigDecimal totalAmount = input.getUnitPrice() != null
                    ? input.getUnitPrice().multiply(BigDecimal.valueOf(input.getQuantity()))
                    : null;

            details.add(OutboundDetailEntity.builder()
                    .outbound(outbound)
                    .product(product)
                    .lot(lot)
                    .location(location)
                    .quantity(input.getQuantity())
                    .overrideReason(input.getOverrideReason())
                    .unitPrice(input.getUnitPrice())
                    .totalAmount(totalAmount)
                    .build());
        }

        details = outboundDetailRepository.saveAll(details);

        return OutboundMapper.toResponse(outbound, details);
    }

    @Override
    @Transactional
    public void voidOutbound(Integer id, String reason, User actor) {
        OutboundEntity outbound = outboundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu xuất"));

        if (outbound.getStatus() == DocumentStatusEnum.VOIDED) {
            throw new IllegalOperationException("Phiếu đã bị hủy trước đó");
        }

        UserEntity voidedBy = findUserOrThrow(actor);

        // BR-19 (SRS 1.3.1): quyền hủy phiếu CHỈ thuộc ACCOUNTANT và ADMIN (không phải
        // "cấp cao hơn người lập" chung chung nữa - MANAGER/STAFF không được hủy dù cấp gì).
        // Người hủy phải khác người lập (BR-04).
        if (voidedBy.getId().equals(outbound.getCreatedBy().getId())) {
            throw new IllegalOperationException("Người hủy phải khác người lập phiếu");
        }
        if (voidedBy.getRole() != RoleEnum.ACCOUNTANT && voidedBy.getRole() != RoleEnum.ADMIN) {
            throw new IllegalOperationException("Chỉ ACCOUNTANT hoặc ADMIN được hủy phiếu");
        }

        List<OutboundDetailEntity> details = outboundDetailRepository.findByOutbound_IdIn(List.of(id));
        for (OutboundDetailEntity detail : details) {
            inventoryService.updateInventory(
                    outbound.getWarehouse().getId(),
                    detail.getProduct().getId(),
                    detail.getLot().getId(),
                    detail.getLocation().getId(),
                    detail.getQuantity(),
                    RefTypeEnum.OUTBOUND,
                    outbound.getId(),
                    voidedBy.getId());
        }

        outbound.setStatus(DocumentStatusEnum.VOIDED);
        outbound.setVoidReason(reason);
        outbound.setVoidedBy(voidedBy);
        outbound.setVoidedAt(LocalDateTime.now());
        outboundRepository.save(outbound);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutboundResponse> getByWarehouseId(Integer warehouseId) {
        List<OutboundEntity> outbounds = outboundRepository.findByWarehouseId(warehouseId);

        Map<Integer, List<OutboundDetailEntity>> detailsByOutboundId = outboundDetailRepository
                .findByOutbound_IdIn(outbounds.stream().map(OutboundEntity::getId).toList())
                .stream()
                .collect(Collectors.groupingBy(d -> d.getOutbound().getId()));

        return outbounds.stream()
                .map(o -> OutboundMapper.toResponse(o, detailsByOutboundId.getOrDefault(o.getId(), List.of())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OutboundResponse getById(Integer id) {
        OutboundEntity outbound = outboundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu xuất"));
        List<OutboundDetailEntity> details = outboundDetailRepository.findByOutbound_IdIn(List.of(id));
        return OutboundMapper.toResponse(outbound, details);
    }

    private UserEntity findUserOrThrow(User actor) {
        return userRepository.findByEmail(actor.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + actor.getUsername()));
    }
}