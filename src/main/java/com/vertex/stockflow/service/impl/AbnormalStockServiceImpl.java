package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.AbnormalReasonEnum;
import com.vertex.stockflow.common.enums.ApprovalStatusEnum;
import com.vertex.stockflow.common.enums.AuditAction;
import com.vertex.stockflow.common.enums.RefTypeEnum;
import com.vertex.stockflow.common.util.ApprovalPolicy;
import com.vertex.stockflow.dto.request.AbnormalStockCreateRequest;
import com.vertex.stockflow.dto.request.AbnormalStockDetailInput;
import com.vertex.stockflow.dto.request.AbnormalStockRejectRequest;
import com.vertex.stockflow.dto.response.AbnormalStockResponse;
import com.vertex.stockflow.entity.*;
import com.vertex.stockflow.exception.IllegalOperationException;
import com.vertex.stockflow.exception.ResourceNotFoundException;
import com.vertex.stockflow.mapper.AbnormalStockMapper;
import com.vertex.stockflow.repository.*;
import com.vertex.stockflow.service.AbnormalStockService;
import com.vertex.stockflow.service.AuditLogService;
import com.vertex.stockflow.service.InventoryService;
import lombok.RequiredArgsConstructor;
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
public class AbnormalStockServiceImpl implements AbnormalStockService {

    private final WarehouseRepository warehouseRepository;
    private final LotRepository lotRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final AbnormalStockRepository abnormalStockRepository;
    private final AbnormalStockDetailRepository abnormalStockDetailRepository;
    private final StocktakeDetailRepository stocktakeDetailRepository;
    private final InventoryService inventoryService;
    private final AuditLogService auditLogService;
    private final ApprovalPolicy approvalPolicy;

    @Override
    @Transactional
    public AbnormalStockResponse create(AbnormalStockCreateRequest request, User actor) {
        WarehouseEntity warehouse = findWarehouseOrThrow(request.getWarehouseId());
        UserEntity createdBy = findUserOrThrow(actor);

        List<AbnormalStockDetailEntity> details = new ArrayList<>();
        for (AbnormalStockDetailInput input : request.getDetails()) {
            if (input.getReasonType() == AbnormalReasonEnum.OTHER
                    && (input.getNote() == null || input.getNote().isBlank())) {
                throw new IllegalOperationException("Bắt buộc nhập ghi chú khi lý do là OTHER");
            }

            LotEntity lot = lotRepository.findById(input.getLotId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lô hàng id: " + input.getLotId()));
            ProductEntity product = lot.getProduct();

            // Quyết định #5 (Bước 0, ngoài SRS): chặn nếu lô đang có phiếu kiểm kê PENDING.
            if (stocktakeDetailRepository.existsByLot_IdAndStocktake_Status(lot.getId(), ApprovalStatusEnum.PENDING)) {
                throw new IllegalOperationException(
                        "Lô " + lot.getLotCode() + " đang có phiếu kiểm kê chờ duyệt, không thể tạo phiếu hàng bất thường");
            }

            // Validate tổ hợp (warehouse, product, lot, location) có thật trong tồn kho -
            // không suy luận ngầm locationId, client phải gửi rõ (điều chỉnh quyết định #1).
            InventoryEntity inventory = inventoryRepository
                    .findByWarehouseIdAndProductIdAndLotIdAndLocationId(
                            warehouse.getId(), product.getId(), input.getLotId(), input.getLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy tồn kho cho lô " + lot.getLotCode() + " tại vị trí đã chọn"));
            StorageLocationEntity location = inventory.getLocation();

            details.add(AbnormalStockDetailEntity.builder()
                    .product(product)
                    .lot(lot)
                    .location(location)
                    .quantity(input.getQuantity())
                    .reasonType(input.getReasonType())
                    .note(input.getNote())
                    .build());
        }

        AbnormalStockEntity abnormalStock = AbnormalStockEntity.builder()
                .code("TEMP")
                .warehouse(warehouse)
                .createdBy(createdBy)
                .status(ApprovalStatusEnum.PENDING)
                .build();
        abnormalStock = abnormalStockRepository.save(abnormalStock);

        // Không cần gọi save() lần 2 - entity đang managed trong transaction này, Hibernate tự
        // flush UPDATE lúc commit nhờ dirty checking (cùng lý do đã áp dụng ở StocktakeServiceImpl).
        abnormalStock.setCode("BT-" + String.format("%06d", abnormalStock.getId()));

        for (AbnormalStockDetailEntity detail : details) {
            detail.setAbnormalStock(abnormalStock);
        }
        details = abnormalStockDetailRepository.saveAll(details);

        return AbnormalStockMapper.toResponse(abnormalStock, details);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AbnormalStockResponse> getByWarehouseId(Integer warehouseId) {
        findWarehouseOrThrow(warehouseId);
        List<AbnormalStockEntity> abnormalStocks = abnormalStockRepository.findByWarehouseId(warehouseId);

        // Gom chi tiết của TẤT CẢ phiếu trong 1 query duy nhất rồi group theo id trong bộ nhớ,
        // thay vì gọi findByAbnormalStockId riêng cho từng phiếu (N+1) trong vòng lặp bên dưới.
        Map<Integer, List<AbnormalStockDetailEntity>> detailsByAbnormalStockId = abnormalStockDetailRepository
                .findByAbnormalStock_IdIn(abnormalStocks.stream().map(AbnormalStockEntity::getId).toList())
                .stream()
                .collect(Collectors.groupingBy(d -> d.getAbnormalStock().getId()));

        return abnormalStocks.stream()
                .map(as -> AbnormalStockMapper.toResponse(as,
                        detailsByAbnormalStockId.getOrDefault(as.getId(), List.of())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AbnormalStockResponse getById(Integer id) {
        AbnormalStockEntity abnormalStock = findAbnormalStockOrThrow(id);
        return AbnormalStockMapper.toResponse(abnormalStock, abnormalStockDetailRepository.findByAbnormalStockId(id));
    }

    @Override
    @Transactional
    public AbnormalStockResponse approve(Integer id, User actor) {
        AbnormalStockEntity abnormalStock = findAbnormalStockOrThrow(id);
        if (abnormalStock.getStatus() != ApprovalStatusEnum.PENDING) {
            throw new IllegalOperationException("Phiếu đã được xử lý, không thể duyệt lại");
        }

        UserEntity approver = findUserOrThrow(actor);
        approvalPolicy.validateApprover(abnormalStock.getCreatedBy(), approver);

        List<AbnormalStockDetailEntity> details = abnormalStockDetailRepository.findByAbnormalStockId(id);
        for (AbnormalStockDetailEntity detail : details) {
            // Quy ước dấu (Bước 12): quantity lưu dương, luôn diễn giải là số cần TRỪ khỏi tồn kho -
            // cả 4 lý do (DAMAGED/LOST/EXPIRED/OTHER) đều là hao hụt, không có chiều tăng tồn.
            inventoryService.updateInventory(
                    abnormalStock.getWarehouse().getId(),
                    detail.getProduct().getId(),
                    detail.getLot().getId(),
                    detail.getLocation().getId(),
                    -detail.getQuantity(),
                    RefTypeEnum.ABNORMAL,
                    abnormalStock.getId(),
                    approver.getId());
        }

        abnormalStock.setStatus(ApprovalStatusEnum.APPROVED);
        abnormalStock.setApprovedBy(approver);
        abnormalStock.setApprovedAt(LocalDateTime.now());
        abnormalStock = abnormalStockRepository.save(abnormalStock);

        auditLogService.log(actor, AuditAction.ABNORMAL_STOCK_APPROVE, "abnormal_stocks", id,
                "Duyệt phiếu hàng bất thường " + abnormalStock.getCode());

        return AbnormalStockMapper.toResponse(abnormalStock, details);
    }

    @Override
    @Transactional
    public AbnormalStockResponse reject(Integer id, AbnormalStockRejectRequest request, User actor) {
        AbnormalStockEntity abnormalStock = findAbnormalStockOrThrow(id);
        if (abnormalStock.getStatus() != ApprovalStatusEnum.PENDING) {
            throw new IllegalOperationException("Phiếu đã được xử lý, không thể từ chối");
        }

        UserEntity approver = findUserOrThrow(actor);
        approvalPolicy.validateApprover(abnormalStock.getCreatedBy(), approver);

        abnormalStock.setStatus(ApprovalStatusEnum.REJECTED);
        abnormalStock.setApprovedBy(approver);
        abnormalStock.setApprovedAt(LocalDateTime.now());
        abnormalStock.setRejectReason(request.getRejectReason());
        abnormalStock = abnormalStockRepository.save(abnormalStock);

        auditLogService.log(actor, AuditAction.ABNORMAL_STOCK_REJECT, "abnormal_stocks", id,
                "Từ chối phiếu hàng bất thường " + abnormalStock.getCode() + ": " + request.getRejectReason());

        List<AbnormalStockDetailEntity> details = abnormalStockDetailRepository.findByAbnormalStockId(id);
        return AbnormalStockMapper.toResponse(abnormalStock, details);
    }

    private WarehouseEntity findWarehouseOrThrow(Integer warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kho id: " + warehouseId));
    }

    private UserEntity findUserOrThrow(User actor) {
        return userRepository.findByEmail(actor.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + actor.getUsername()));
    }

    private AbnormalStockEntity findAbnormalStockOrThrow(Integer id) {
        return abnormalStockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu hàng bất thường id: " + id));
    }
}
