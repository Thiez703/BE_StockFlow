package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.ApprovalStatusEnum;
import com.vertex.stockflow.common.enums.AuditAction;
import com.vertex.stockflow.common.enums.RefTypeEnum;
import com.vertex.stockflow.common.util.ApprovalPolicy;
import com.vertex.stockflow.dto.request.StocktakeCreateRequest;
import com.vertex.stockflow.dto.request.StocktakeDetailInput;
import com.vertex.stockflow.dto.request.StocktakeRejectRequest;
import com.vertex.stockflow.dto.response.StocktakeResponse;
import com.vertex.stockflow.dto.response.StorageMapRawRow;
import com.vertex.stockflow.entity.*;
import com.vertex.stockflow.exception.IllegalOperationException;
import com.vertex.stockflow.exception.ResourceNotFoundException;
import com.vertex.stockflow.mapper.StocktakeMapper;
import com.vertex.stockflow.repository.*;
import com.vertex.stockflow.service.AuditLogService;
import com.vertex.stockflow.service.InventoryService;
import com.vertex.stockflow.service.StocktakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StocktakeServiceImpl implements StocktakeService {

    private final WarehouseRepository warehouseRepository;
    private final LotRepository lotRepository;
    private final InventoryRepository inventoryRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final UserRepository userRepository;
    private final StocktakeRepository stocktakeRepository;
    private final StocktakeDetailRepository stocktakeDetailRepository;
    private final InventoryService inventoryService;
    private final AuditLogService auditLogService;
    private final ApprovalPolicy approvalPolicy;

    @Override
    @Transactional(readOnly = true)
    public List<StorageMapRawRow> getInventorySnapshot(Integer warehouseId) {
        findWarehouseOrThrow(warehouseId);
        return storageLocationRepository.findStorageMapByWarehouseId(warehouseId).stream()
                .filter(row -> row.getLotId() != null && row.getQuantity() != null && row.getQuantity() > 0)
                .toList();
    }

    @Override
    @Transactional
    public StocktakeResponse create(StocktakeCreateRequest request, User actor) {
        WarehouseEntity warehouse = findWarehouseOrThrow(request.getWarehouseId());
        UserEntity createdBy = findUserOrThrow(actor);

        List<StocktakeDetailEntity> details = new ArrayList<>();
        for (StocktakeDetailInput input : request.getDetails()) {
            LotEntity lot = lotRepository.findById(input.getLotId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lô hàng id: " + input.getLotId()));
            ProductEntity product = lot.getProduct();

            InventoryEntity inventory = inventoryRepository
                    .findByWarehouseIdAndProductIdAndLotIdAndLocationId(
                            warehouse.getId(), product.getId(), input.getLotId(), input.getLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy tồn kho cho lô " + lot.getLotCode() + " tại vị trí đã chọn"));

            int damagedQty = input.getDamagedQty() != null ? input.getDamagedQty() : 0;
            Integer diffQty = null;

            if (input.getActualQty() != null) {
                if (input.getActualQty() + damagedQty > inventory.getQuantity()) {
                    throw new IllegalOperationException(
                            "Tổng số lượng thực tế (" + input.getActualQty() + ") và hư hỏng (" + damagedQty
                                    + ") vượt quá tồn kho hệ thống (" + inventory.getQuantity()
                                    + ") cho lô " + lot.getLotCode());
                }
                diffQty = input.getActualQty() - inventory.getQuantity();
            }

            details.add(StocktakeDetailEntity.builder()
                    .product(product)
                    .lot(lot)
                    .location(inventory.getLocation())
                    .systemQty(inventory.getQuantity())
                    .actualQty(input.getActualQty())
                    .diffQty(diffQty)
                    .damagedQty(damagedQty)
                    .note(input.getNote())
                    .build());
        }

        boolean isAdmin = actor.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        StocktakeEntity stocktake = StocktakeEntity.builder()
                .code("TEMP")
                .warehouse(warehouse)
                .createdBy(createdBy)
                .status(isAdmin ? ApprovalStatusEnum.APPROVED : ApprovalStatusEnum.PENDING)
                .note(request.getNote())
                .build();

        if (isAdmin) {
            stocktake.setApprovedBy(createdBy);
            stocktake.setApprovedAt(LocalDateTime.now());
        }

        stocktake = stocktakeRepository.save(stocktake);

        // Sinh mã sau khi có id
        stocktake.setCode("KK-" + String.format("%06d", stocktake.getId()));

        for (StocktakeDetailEntity detail : details) {
            detail.setStocktake(stocktake);
            
            if (isAdmin && detail.getDiffQty() != null && detail.getDiffQty() != 0) {
                inventoryService.updateInventory(
                        warehouse.getId(),
                        detail.getProduct().getId(),
                        detail.getLot().getId(),
                        detail.getLocation().getId(),
                        detail.getDiffQty(),
                        RefTypeEnum.STOCKTAKE,
                        stocktake.getId(),
                        createdBy.getId());
            }
        }
        details = stocktakeDetailRepository.saveAll(details);

        if (isAdmin) {
            auditLogService.log(actor, AuditAction.STOCKTAKE_APPROVE, "stocktakes", stocktake.getId(),
                    "Tự động duyệt phiếu kiểm kê " + stocktake.getCode() + " do người lập là ADMIN");
        }

        return StocktakeMapper.toResponse(stocktake, details);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StocktakeResponse> getByWarehouseId(Integer warehouseId, Pageable pageable, User actor) {
        findWarehouseOrThrow(warehouseId);
        UserEntity currentUser = findUserOrThrow(actor);
        
        Page<StocktakeEntity> stocktakePage;
        if (actor.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"))) {
            stocktakePage = stocktakeRepository.findByWarehouseIdAndCreatedById(warehouseId, currentUser.getId(), pageable);
        } else {
            stocktakePage = stocktakeRepository.findByWarehouseId(warehouseId, pageable);
        }

        Map<Integer, List<StocktakeDetailEntity>> detailsByStocktakeId = stocktakeDetailRepository
                .findByStocktake_IdIn(stocktakePage.getContent().stream().map(StocktakeEntity::getId).toList())
                .stream()
                .collect(Collectors.groupingBy(d -> d.getStocktake().getId()));

        return stocktakePage.map(st -> StocktakeMapper.toResponse(st,
                detailsByStocktakeId.getOrDefault(st.getId(), List.of())));
    }

    @Override
    @Transactional(readOnly = true)
    public StocktakeResponse getById(Integer id, User actor) {
        StocktakeEntity stocktake = findStocktakeOrThrow(id);
        
        boolean isOnlyStaff = actor.getAuthorities().stream()
                .allMatch(a -> a.getAuthority().equals("ROLE_STAFF"));
        if (isOnlyStaff && !stocktake.getCreatedBy().getEmail().equals(actor.getUsername())) {
            throw new org.springframework.security.access.AccessDeniedException("Bạn không có quyền xem phiếu kiểm kê của người khác");
        }
        
        return StocktakeMapper.toResponse(stocktake, stocktakeDetailRepository.findByStocktakeId(id));
    }

    @Override
    @Transactional
    public StocktakeResponse approve(Integer id, User actor) {
        StocktakeEntity stocktake = findStocktakeOrThrow(id);
        if (stocktake.getStatus() != ApprovalStatusEnum.PENDING) {
            throw new IllegalOperationException("Phiếu đã được xử lý, không thể duyệt lại");
        }

        UserEntity approver = findUserOrThrow(actor);
        approvalPolicy.validateApprover(stocktake.getCreatedBy(), approver);

        List<StocktakeDetailEntity> details = stocktakeDetailRepository.findByStocktakeId(id);
        for (StocktakeDetailEntity detail : details) {
            if (detail.getDiffQty() != null && detail.getDiffQty() != 0) {
                inventoryService.updateInventory(
                        stocktake.getWarehouse().getId(),
                        detail.getProduct().getId(),
                        detail.getLot().getId(),
                        detail.getLocation().getId(),
                        detail.getDiffQty(),
                        RefTypeEnum.STOCKTAKE,
                        stocktake.getId(),
                        approver.getId());
            }
        }

        stocktake.setStatus(ApprovalStatusEnum.APPROVED);
        stocktake.setApprovedBy(approver);
        stocktake.setApprovedAt(LocalDateTime.now());
        stocktake = stocktakeRepository.save(stocktake);

        auditLogService.log(actor, AuditAction.STOCKTAKE_APPROVE, "stocktakes", id,
                "Duyệt phiếu kiểm kê " + stocktake.getCode());

        return StocktakeMapper.toResponse(stocktake, details);
    }

    @Override
    @Transactional
    public StocktakeResponse reject(Integer id, StocktakeRejectRequest request, User actor) {
        StocktakeEntity stocktake = findStocktakeOrThrow(id);
        if (stocktake.getStatus() != ApprovalStatusEnum.PENDING) {
            throw new IllegalOperationException("Phiếu đã được xử lý, không thể từ chối");
        }

        UserEntity approver = findUserOrThrow(actor);
        approvalPolicy.validateApprover(stocktake.getCreatedBy(), approver);

        stocktake.setStatus(ApprovalStatusEnum.REJECTED);
        stocktake.setApprovedBy(approver);
        stocktake.setApprovedAt(LocalDateTime.now());
        stocktake.setRejectReason(request.getRejectReason());
        stocktake = stocktakeRepository.save(stocktake);

        auditLogService.log(actor, AuditAction.STOCKTAKE_REJECT, "stocktakes", id,
                "Từ chối phiếu kiểm kê " + stocktake.getCode() + ": " + request.getRejectReason());

        List<StocktakeDetailEntity> details = stocktakeDetailRepository.findByStocktakeId(id);
        return StocktakeMapper.toResponse(stocktake, details);
    }

    private WarehouseEntity findWarehouseOrThrow(Integer warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kho id: " + warehouseId));
    }

    private UserEntity findUserOrThrow(User actor) {
        return userRepository.findByEmail(actor.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy thông tin người dùng. Phiên đăng nhập có thể đã hết hạn."));
    }

    protected StocktakeEntity findStocktakeOrThrow(Integer id) {
        return stocktakeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu kiểm kê id: " + id));
    }
}
