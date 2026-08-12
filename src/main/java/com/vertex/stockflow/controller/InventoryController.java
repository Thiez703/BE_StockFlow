package com.vertex.stockflow.controller;

import com.vertex.stockflow.dto.response.InventoryResponse;
import com.vertex.stockflow.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','ACCOUNTANT','STAFF')")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<Page<InventoryResponse>> search(
            @RequestParam(required = false) Integer productId,
            @RequestParam(required = false) Integer lotId,
            @RequestParam(required = false) Integer locationId,
            Pageable pageable) {
        return ResponseEntity.ok(inventoryService.search(productId, lotId, locationId, pageable));
    }
}
