package com.vertex.stockflow.controller;

import com.vertex.stockflow.dto.request.StocktakeCreateRequest;
import com.vertex.stockflow.dto.request.StocktakeRejectRequest;
import com.vertex.stockflow.dto.response.StocktakeResponse;
import com.vertex.stockflow.dto.response.StorageMapCellResponse;
import com.vertex.stockflow.service.StocktakeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocktakes")
@RequiredArgsConstructor
public class StocktakeController {

    private final StocktakeService stocktakeService;

    // Khung tồn kho hiện tại của kho để FE dựng form đếm trước khi tạo phiếu (quyết định #2).
    @GetMapping("/inventory-snapshot")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','ACCOUNTANT','STAFF')")
    public ResponseEntity<List<StorageMapCellResponse>> getInventorySnapshot(@RequestParam Integer warehouseId) {
        return ResponseEntity.ok(stocktakeService.getInventorySnapshot(warehouseId));
    }

    // Lập phiếu kiểm kê — ACCOUNTANT không lập chứng từ (SRS §2.4).
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<StocktakeResponse> create(@Valid @RequestBody StocktakeCreateRequest request,
                                                      @AuthenticationPrincipal User actor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stocktakeService.create(request, actor));
    }

    // Danh sách phiếu kiểm kê — STAFF chỉ xem phiếu do mình lập.
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','ACCOUNTANT','STAFF')")
    public ResponseEntity<Page<StocktakeResponse>> getByWarehouseId(@RequestParam Integer warehouseId,
                                                                     Pageable pageable,
                                                                     @AuthenticationPrincipal User actor) {
        return ResponseEntity.ok(stocktakeService.getByWarehouseId(warehouseId, pageable, actor));
    }

    // Chi tiết phiếu — mở cho mọi role đã xác thực (STAFF xem phiếu do mình lập).
    @GetMapping("/{id}")
    public ResponseEntity<StocktakeResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(stocktakeService.getById(id));
    }

    // Duyệt/từ chối - ma trận D (BR-05): chỉ ADMIN và ACCOUNTANT.
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT')")
    public ResponseEntity<StocktakeResponse> approve(@PathVariable Integer id, @AuthenticationPrincipal User actor) {
        return ResponseEntity.ok(stocktakeService.approve(id, actor));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT')")
    public ResponseEntity<StocktakeResponse> reject(@PathVariable Integer id,
                                                      @Valid @RequestBody StocktakeRejectRequest request,
                                                      @AuthenticationPrincipal User actor) {
        return ResponseEntity.ok(stocktakeService.reject(id, request, actor));
    }
}
