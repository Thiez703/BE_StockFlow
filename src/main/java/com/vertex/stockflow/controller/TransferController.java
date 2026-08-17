package com.vertex.stockflow.controller;

import com.vertex.stockflow.dto.request.TransferCreateRequest;
import com.vertex.stockflow.dto.response.TransferResponse;
import com.vertex.stockflow.service.TransferService;
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
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF','MANAGER','ADMIN')")
    public ResponseEntity<TransferResponse> create(@Valid @RequestBody TransferCreateRequest request,
                                                    @AuthenticationPrincipal User actor) {
        return ResponseEntity.ok(transferService.create(request, actor));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF','ACCOUNTANT','MANAGER','ADMIN')")
    public ResponseEntity<Page<TransferResponse>> search(
            @RequestParam(required = false) Integer warehouseId,
            Pageable pageable) {
        return ResponseEntity.ok(transferService.search(warehouseId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','ACCOUNTANT','MANAGER','ADMIN')")
    public ResponseEntity<TransferResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(transferService.getById(id));
    }
}
