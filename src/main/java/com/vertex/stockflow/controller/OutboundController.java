package com.vertex.stockflow.controller;

import com.vertex.stockflow.dto.request.OutboundCreateRequest;
import com.vertex.stockflow.dto.request.OutboundVoidRequest;
import com.vertex.stockflow.dto.response.OutboundResponse;
import com.vertex.stockflow.service.OutboundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/outbounds")
@RequiredArgsConstructor
public class OutboundController {

    private final OutboundService outboundService;

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF','MANAGER','ADMIN')")
    public ResponseEntity<OutboundResponse> create(@Valid @RequestBody OutboundCreateRequest request,
                                                   @AuthenticationPrincipal User actor) {
        return ResponseEntity.ok(outboundService.create(request, actor));
    }

    @PatchMapping("/{id}/void")
    @PreAuthorize("hasAnyRole('ACCOUNTANT','ADMIN')")
    public ResponseEntity<Void> voidOutbound(@PathVariable Integer id,
                                             @Valid @RequestBody OutboundVoidRequest request,
                                             @AuthenticationPrincipal User actor) {
        outboundService.voidOutbound(id, request.getReason(), actor);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF','ACCOUNTANT','MANAGER','ADMIN')")
    public ResponseEntity<Page<OutboundResponse>> getByWarehouse(@RequestParam Integer warehouseId,
                                                                  Pageable pageable) {
        return ResponseEntity.ok(outboundService.getByWarehouseId(warehouseId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','ACCOUNTANT','MANAGER','ADMIN')")
    public ResponseEntity<OutboundResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(outboundService.getById(id));
    }
}