package com.vertex.stockflow.controller;

import com.vertex.stockflow.dto.request.StorageLocationCreateRequest;
import com.vertex.stockflow.dto.request.StorageLocationUpdateRequest;
import com.vertex.stockflow.dto.response.StorageLocationResponse;
import com.vertex.stockflow.service.StorageLocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/storage-locations")
@RequiredArgsConstructor
public class StorageLocationController {

    private final StorageLocationService storageLocationService;

    @PostMapping
    public ResponseEntity<StorageLocationResponse> create(@Valid @RequestBody StorageLocationCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(storageLocationService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StorageLocationResponse> update(@PathVariable Integer id,
                                                             @Valid @RequestBody StorageLocationUpdateRequest request) {
        return ResponseEntity.ok(storageLocationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        storageLocationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StorageLocationResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(storageLocationService.getById(id));
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<StorageLocationResponse>> getByWarehouseId(@PathVariable Integer warehouseId) {
        return ResponseEntity.ok(storageLocationService.getByWarehouseId(warehouseId));
    }

    @GetMapping
    public ResponseEntity<List<StorageLocationResponse>> getAll() {
        return ResponseEntity.ok(storageLocationService.getAll());
    }
}
