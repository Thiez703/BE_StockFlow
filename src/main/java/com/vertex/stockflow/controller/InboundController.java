package com.vertex.stockflow.controller;

import com.vertex.stockflow.common.enums.DocumentStatusEnum;
import com.vertex.stockflow.dto.request.InboundCreateRequest;
import com.vertex.stockflow.dto.request.InboundVoidRequest;
import com.vertex.stockflow.dto.response.InboundResponse;
import com.vertex.stockflow.service.InboundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/inbounds")
@RequiredArgsConstructor
public class InboundController {

    private final InboundService inboundService;

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF','MANAGER','ADMIN')")
    public ResponseEntity<InboundResponse> create(@Valid @RequestBody InboundCreateRequest request,
                                                   @AuthenticationPrincipal User actor) {
        return ResponseEntity.ok(inboundService.create(request, actor));
    }

    @PatchMapping("/{id}/void")
    @PreAuthorize("hasAnyRole('ACCOUNTANT','ADMIN')")
    public ResponseEntity<Void> voidInbound(@PathVariable Integer id,
                                             @Valid @RequestBody InboundVoidRequest request,
                                             @AuthenticationPrincipal User actor) {
        inboundService.voidInbound(id, request.getReason(), actor);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF','ACCOUNTANT','MANAGER','ADMIN')")
    public ResponseEntity<Page<InboundResponse>> search(
            @RequestParam(required = false) Integer warehouseId,
            @RequestParam(required = false) Integer supplierId,
            @RequestParam(required = false) DocumentStatusEnum status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            Pageable pageable) {
        LocalDateTime fromDt = null;
        if (from != null && from.length() >= 10) {
            fromDt = LocalDate.parse(from.substring(0, 10)).atStartOfDay();
        }
        LocalDateTime toDt = null;
        if (to != null && to.length() >= 10) {
            toDt = LocalDate.parse(to.substring(0, 10)).atTime(LocalTime.MAX);
        }
        return ResponseEntity.ok(inboundService.search(warehouseId, supplierId, status, fromDt, toDt, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','ACCOUNTANT','MANAGER','ADMIN')")
    public ResponseEntity<InboundResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(inboundService.getById(id));
    }
}
