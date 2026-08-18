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
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) RefTypeEnum refType,
            Pageable pageable) {
        LocalDateTime fromDt = null;
        if (from != null && from.length() >= 10) {
            fromDt = LocalDate.parse(from.substring(0, 10)).atStartOfDay();
        }
        LocalDateTime toDt = null;
        if (to != null && to.length() >= 10) {
            toDt = LocalDate.parse(to.substring(0, 10)).atTime(LocalTime.MAX);
        }
        return ResponseEntity.ok(inventoryTransactionService.search(productId, lotId, locationId, fromDt, toDt, refType, pageable));
    }

    @GetMapping("/by-product/{productId}")
    public ResponseEntity<Page<InventoryTransactionResponse>> getByProduct(
            @PathVariable Integer productId,
            Pageable pageable) {
        return ResponseEntity.ok(inventoryTransactionService.getByProduct(productId, pageable));
    }
}
