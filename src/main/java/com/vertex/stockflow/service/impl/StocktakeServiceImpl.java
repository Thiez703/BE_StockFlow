package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.ApprovalStatusEnum;
import com.vertex.stockflow.common.enums.AuditAction;
import com.vertex.stockflow.common.enums.RefTypeEnum;
import com.vertex.stockflow.common.util.ApprovalPolicy;
import com.vertex.stockflow.dto.request.StocktakeCreateRequest;
import com.vertex.stockflow.dto.request.StocktakeDetailInput;
import com.vertex.stockflow.dto.request.StocktakeRejectRequest;
import com.vertex.stockflow.dto.response.StocktakeResponse;
import com.vertex.stockflow.dto.response.StorageMapCellResponse;
import com.vertex.stockflow.entity.*;
import com.vertex.stockflow.exception.IllegalOperationException;
import com.vertex.stockflow.exception.ResourceNotFoundException;
import com.vertex.stockflow.mapper.StocktakeMapper;
import com.vertex.stockflow.repository.*;
import com.vertex.stockflow.service.AuditLogService;
import com.vertex.stockflow.service.InventoryService;
import com.vertex.stockflow.service.StocktakeService;
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
public class StocktakeServiceImpl implements StocktakeService {

    private final WarehouseRepository warehouseRepository;
    private final LotRepository lotRepository;
    private final InventoryRepository inventoryRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final UserRepository userRepository;
    private final StocktakeRepository stocktakeRepository;
    private final StocktakeDetailRepository stocktakeDetailRepository;
    private final AbnormalStockDetailRepository abnormalStockDetailRepository;
    private final InventoryService inventoryService;
    private final AuditLogService auditLogService;
    private final ApprovalPolicy approvalPolicy;

    @Override
    @Transactional(readOnly = true)
    public List<StorageMapCellResponse> getInventorySnapshot(Integer warehouseId) {
        findWarehouseOrThrow(warehouseId);
        // Tái dùng query đã có sẵn cho Dashboard (storage-map) thay vì viết query mới -
        // nó đã trả đúng dữ liệu cần: location, lot, product, quantity theo từng vị trí.
        // Lọc bỏ ô trống (không có lô) vì không có gì để đếm, và lọc bỏ lô đang bị khóa bởi
        // phiếu hàng bất thường PENDING khác - đối xứng với check ở create(), để người dùng
        // không đếm nhầm một dòng rồi mới bị chặn lúc submit (quyết định #5, Bước 0).
        return storageLocationRepository.findStorageMapByWarehouseId(warehouseId).stream()
                .filter(cell -> cell.getLotId() != null)
                .filter(cell -> !abnormalStockDetailRepository
                        .existsByLot_IdAndAbnormalStock_Status(cell.getLotId(), ApprovalStatusEnum.PENDING))
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

            // Quyết định #5 (Bước 0, ngoài SRS): chặn nếu lô đang có phiếu hàng bất thường PENDING.
            if (abnormalStockDetailRepository.existsByLot_IdAndAbnormalStock_Status(lot.getId(), ApprovalStatusEnum.PENDING)) {
                throw new IllegalOperationException(
                        "Lô " + lot.getLotCode() + " đang có phiếu hàng bất thường chờ duyệt, không thể tạo phiếu kiểm kê");
            }

            InventoryEntity inventory = inventoryRepository
                    .findByWarehouseIdAndProductIdAndLotIdAndLocationId(
                            warehouse.getId(), product.getId(), input.getLotId(), input.getLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy tồn kho cho lô " + lot.getLotCode() + " tại vị trí đã chọn"));

            int diffQty = input.getActualQty() - inventory.getQuantity();

            details.add(StocktakeDetailEntity.builder()
                    .product(product)
                    .lot(lot)
                    .location(inventory.getLocation())
                    .systemQty(inventory.getQuantity())
                    .actualQty(input.getActualQty())
                    .diffQty(diffQty)
                    .build());
        }

        StocktakeEntity stocktake = StocktakeEntity.builder()
                .code("TEMP")
                .warehouse(warehouse)
                .createdBy(createdBy)
                .status(ApprovalStatusEnum.PENDING)
                .note(request.getNote())
                .build();
        stocktake = stocktakeRepository.save(stocktake);

        // Sinh mã sau khi có id (giống cách StorageLocationServiceImpl sinh locationCode từ tọa độ) -
        // đơn giản, không cần bảng sequence riêng. Không cần gọi save() lần 2: entity đang managed
        // trong transaction này, Hibernate tự phát hiện thay đổi (dirty checking) và flush UPDATE
        // lúc commit - gọi save() lần nữa chỉ khiến code trông như phải làm thủ công.
        stocktake.setCode("KK-" + String.format("%06d", stocktake.getId()));

        for (StocktakeDetailEntity detail : details) {
            detail.setStocktake(stocktake);
        }
        details = stocktakeDetailRepository.saveAll(details);

        return StocktakeMapper.toResponse(stocktake, details);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StocktakeResponse> getByWarehouseId(Integer warehouseId) {
        findWarehouseOrThrow(warehouseId);
        List<StocktakeEntity> stocktakes = stocktakeRepository.findByWarehouseId(warehouseId);

        // Gom chi tiết của TẤT CẢ phiếu trong 1 query duy nhất rồi group theo id trong bộ nhớ,
        // thay vì gọi findByStocktakeId riêng cho từng phiếu (N+1) trong vòng lặp bên dưới.
        Map<Integer, List<StocktakeDetailEntity>> detailsByStocktakeId = stocktakeDetailRepository
                .findByStocktake_IdIn(stocktakes.stream().map(StocktakeEntity::getId).toList())
                .stream()
                .collect(Collectors.groupingBy(d -> d.getStocktake().getId()));

        return stocktakes.stream()
                .map(st -> StocktakeMapper.toResponse(st,
                        detailsByStocktakeId.getOrDefault(st.getId(), List.of())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StocktakeResponse getById(Integer id) {
        StocktakeEntity stocktake = findStocktakeOrThrow(id);
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
            if (detail.getDiffQty() != 0) {
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
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + actor.getUsername()));
    }

    protected StocktakeEntity findStocktakeOrThrow(Integer id) {
        return stocktakeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu kiểm kê id: " + id));
    }
}
