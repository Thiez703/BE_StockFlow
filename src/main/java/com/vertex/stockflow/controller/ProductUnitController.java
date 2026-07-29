package com.vertex.stockflow.controller;

import com.vertex.stockflow.dto.request.ProductUnitRequest;
import com.vertex.stockflow.dto.response.ProductUnitResponse;
import com.vertex.stockflow.service.ProductUnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductUnitController {
    private final ProductUnitService productUnitService;

    @PostMapping("/{productId}/units")
    public ResponseEntity<ProductUnitResponse> createProductUnit(@PathVariable Integer productId, @Valid @RequestBody ProductUnitRequest request) {
        ProductUnitResponse productUnitResponse = productUnitService.create(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(productUnitResponse);
    }

    @PutMapping("/{productId}/units/{id}")
    public ResponseEntity<ProductUnitResponse> updateProductUnit(@PathVariable Integer productId, @PathVariable Integer id, @Valid @RequestBody ProductUnitRequest request) {
        ProductUnitResponse productUnitResponse = productUnitService.update(productId, id, request);
        return ResponseEntity.ok(productUnitResponse);
    }

    @DeleteMapping("/{productId}/units/{id}")
    public ResponseEntity<Void> deleteProductUnit(@PathVariable Integer productId, @PathVariable Integer id) {
        productUnitService.delete(productId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}/units")
    public ResponseEntity<List<ProductUnitResponse>> getProductUnits(@PathVariable Integer productId) {
        List<ProductUnitResponse> response = productUnitService.getByProductId(productId);
        return ResponseEntity.ok(response);
    }

}

