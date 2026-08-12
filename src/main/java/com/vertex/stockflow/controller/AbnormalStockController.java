package com.vertex.stockflow.controller;

import com.vertex.stockflow.dto.request.AbnormalStockCreateRequest;
import com.vertex.stockflow.dto.request.AbnormalStockRejectRequest;
import com.vertex.stockflow.dto.response.AbnormalStockResponse;
import com.vertex.stockflow.service.AbnormalStockService;
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

@RestController
@RequestMapping("/api/abnormal-stocks")
@RequiredArgsConstructor
public class AbnormalStockController {

    private final AbnormalStockService abnormalStockService;

    // Không có endpoint "khung tồn kho" riêng - tái dùng GET /api/stocktakes/inventory-snapshot
    // để tránh viết trùng cùng 1 query ở 2 controller (xem Bước 15 trong kế hoạch).

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<AbnormalStockResponse> create(@Valid @RequestBody AbnormalStockCreateRequest request,
                                                          @AuthenticationPrincipal User actor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(abnormalStockService.create(request, actor));
    }

    // Danh sách phiếu bất thường — STAFF không tra cứu danh sách (ma trận D).
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','ACCOUNTANT')")
    public ResponseEntity<Page<AbnormalStockResponse>> getByWarehouseId(@RequestParam Integer warehouseId,
                                                                        Pageable pageable) {
        return ResponseEntity.ok(abnormalStockService.getByWarehouseId(warehouseId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AbnormalStockResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(abnormalStockService.getById(id));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT')")
    public ResponseEntity<AbnormalStockResponse> approve(@PathVariable Integer id, @AuthenticationPrincipal User actor) {
        return ResponseEntity.ok(abnormalStockService.approve(id, actor));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT')")
    public ResponseEntity<AbnormalStockResponse> reject(@PathVariable Integer id,
                                                          @Valid @RequestBody AbnormalStockRejectRequest request,
                                                          @AuthenticationPrincipal User actor) {
        return ResponseEntity.ok(abnormalStockService.reject(id, request, actor));
    }
}
