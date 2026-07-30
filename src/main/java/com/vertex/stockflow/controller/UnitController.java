package com.vertex.stockflow.controller;

import com.vertex.stockflow.dto.request.UnitRequest;
import com.vertex.stockflow.dto.response.UnitResponse;
import com.vertex.stockflow.service.UnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<UnitResponse> createUnit(@Valid @RequestBody UnitRequest request) {
        UnitResponse unitResponse = unitService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(unitResponse);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<UnitResponse> updateUnit(@PathVariable Integer id, @Valid @RequestBody UnitRequest request) {
        UnitResponse unitResponse = unitService.update(id, request);
        return ResponseEntity.ok(unitResponse);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<UnitResponse> getUnitById(@PathVariable Integer id) {
        UnitResponse unitResponse = unitService.getById(id);
        return ResponseEntity.ok(unitResponse);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping
    public ResponseEntity<List<UnitResponse>> getAllUnits() {
        List<UnitResponse> unitResponses = unitService.getAll();
        return ResponseEntity.ok(unitResponses);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUnit(@PathVariable Integer id) {
        unitService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateUnit(@PathVariable Integer id) {
        unitService.activate(id);
        return ResponseEntity.noContent().build();
    }

}

