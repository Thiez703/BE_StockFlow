package com.vertex.stockflow.controller;

import com.vertex.stockflow.dto.request.StocktakeCreateRequest;
import com.vertex.stockflow.dto.request.StocktakeRejectRequest;
import com.vertex.stockflow.dto.response.StocktakeResponse;
import com.vertex.stockflow.dto.response.StorageMapCellResponse;
import com.vertex.stockflow.service.StocktakeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    @PreAuthorize("hasAnyRole('MANAGER','ACCOUNTANT','STAFF')")
    public ResponseEntity<List<StorageMapCellResponse>> getInventorySnapshot(@RequestParam Integer warehouseId) {
        return ResponseEntity.ok(stocktakeService.getInventorySnapshot(warehouseId));
    }

    // Lập phiếu kiểm kê - ma trận SRS §3.3: ADMIN không có quyền lập (BR-06).
    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER','ACCOUNTANT','STAFF')")
    public ResponseEntity<StocktakeResponse> create(@Valid @RequestBody StocktakeCreateRequest request,
                                                      @AuthenticationPrincipal User actor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stocktakeService.create(request, actor));
    }

    // Xem danh sách/chi tiết mở cho cả 4 role - ADMIN cần xem để duyệt, SRS không có dòng
    // ma trận riêng cho việc xem nên không thể giới hạn giống endpoint tạo phiếu.
    @GetMapping
    public ResponseEntity<List<StocktakeResponse>> getByWarehouseId(@RequestParam Integer warehouseId) {
        return ResponseEntity.ok(stocktakeService.getByWarehouseId(warehouseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StocktakeResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(stocktakeService.getById(id));
    }

    // Duyệt/từ chối - ma trận SRS §3.3: STAFF không có quyền duyệt.
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','ACCOUNTANT')")
    public ResponseEntity<StocktakeResponse> approve(@PathVariable Integer id, @AuthenticationPrincipal User actor) {
        return ResponseEntity.ok(stocktakeService.approve(id, actor));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','ACCOUNTANT')")
    public ResponseEntity<StocktakeResponse> reject(@PathVariable Integer id,
                                                      @Valid @RequestBody StocktakeRejectRequest request,
                                                      @AuthenticationPrincipal User actor) {
        return ResponseEntity.ok(stocktakeService.reject(id, request, actor));
    }
}
