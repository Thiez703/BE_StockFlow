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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) RefTypeEnum refType,
            Pageable pageable) {
        LocalDateTime fromDt = from != null ? from.atStartOfDay() : null;
        LocalDateTime toDt = to != null ? to.atTime(LocalTime.MAX) : null;
        return ResponseEntity.ok(inventoryTransactionService.search(productId, lotId, locationId, fromDt, toDt, refType, pageable));
    }

    @GetMapping("/by-product/{productId}")
    public ResponseEntity<Page<InventoryTransactionResponse>> getByProduct(
            @PathVariable Integer productId,
            Pageable pageable) {
        return ResponseEntity.ok(inventoryTransactionService.getByProduct(productId, pageable));
    }
}
