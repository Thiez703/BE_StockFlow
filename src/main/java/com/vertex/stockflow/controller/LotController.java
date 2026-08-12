package com.vertex.stockflow.controller;

import com.vertex.stockflow.dto.request.LotCreateRequest;
import com.vertex.stockflow.dto.request.LotUpdateRequest;
import com.vertex.stockflow.dto.response.LotResponse;
import com.vertex.stockflow.dto.response.SellThroughRiskResponse;
import com.vertex.stockflow.service.AlertService;
import com.vertex.stockflow.service.LotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lots")
@RequiredArgsConstructor
public class LotController {

    private final LotService lotService;
    private final AlertService alertService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<LotResponse> create(@Valid @RequestBody LotCreateRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(lotService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<LotResponse> update(@PathVariable Integer id,@Valid @RequestBody LotUpdateRequest request){
        return ResponseEntity.ok(lotService.update(id, request));
    }

    @GetMapping
    public ResponseEntity<List<LotResponse>> getAll() {
        return ResponseEntity.ok(lotService.getAll());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<LotResponse>> getByProductId(@PathVariable Integer productId) {
        return ResponseEntity.ok(lotService.getByProductId(productId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<LotResponse> delete(@PathVariable Integer id){
        lotService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{lotId}/sell-through-risk")
    public ResponseEntity<SellThroughRiskResponse> getSellThroughRisk(@PathVariable Integer lotId) {
        return ResponseEntity.ok(alertService.getLotSellThroughRisk(lotId));
    }
}
