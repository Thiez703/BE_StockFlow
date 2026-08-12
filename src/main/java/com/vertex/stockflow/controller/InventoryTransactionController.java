package com.vertex.stockflow.controller;

import com.vertex.stockflow.common.enums.RefTypeEnum;
import com.vertex.stockflow.dto.response.InventoryTransactionResponse;
import com.vertex.stockflow.service.InventoryTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/inventory-transactions")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','ACCOUNTANT')")
public class InventoryTransactionController {

    private final InventoryTransactionService inventoryTransactionService;

    @GetMapping
    public ResponseEntity<Page<InventoryTransactionResponse>> search(
            @RequestParam(required = false) Integer productId,
            @RequestParam(required = false) Integer lotId,
            @RequestParam(required = false) Integer locationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) RefTypeEnum refType,
            Pageable pageable) {
        return ResponseEntity.ok(inventoryTransactionService.search(productId, lotId, locationId, from, to, refType, pageable));
    }

    @GetMapping("/by-product/{productId}")
    public ResponseEntity<Page<InventoryTransactionResponse>> getByProduct(
            @PathVariable Integer productId,
            Pageable pageable) {
        return ResponseEntity.ok(inventoryTransactionService.getByProduct(productId, pageable));
    }
}
