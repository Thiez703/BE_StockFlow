package com.vertex.stockflow.controller;

import com.vertex.stockflow.dto.response.StorageMapResponse;
import com.vertex.stockflow.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Sơ đồ vị trí lô hàng cho trang Bảng điều khiển.
     *
     * Chặn STAFF theo ma trận phân quyền SRS v1.3.1:
     * sơ đồ này hiện cả số tồn lẫn cảnh báo hết hạn - 2 mục STAFF không được xem.
     * FE cũng phải ẩn hẳn khối này với STAFF, đừng để gọi rồi ăn 403.
     */
    @GetMapping("/storage-map")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','ACCOUNTANT')")
    public ResponseEntity<StorageMapResponse> getStorageMap(@RequestParam Integer warehouseId) {
        return ResponseEntity.ok(dashboardService.getStorageMap(warehouseId));
    }
}