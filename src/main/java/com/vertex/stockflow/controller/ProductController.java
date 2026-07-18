package com.vertex.stockflow.controller;

import com.vertex.stockflow.dto.request.ProductRequest;
import com.vertex.stockflow.dto.response.ProductResponse;
import com.vertex.stockflow.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CỬA NGÕ API của Product: nhận HTTP request từ FE và trả JSON.
 * Chỉ điều phối (nhận -> gọi Service -> trả kết quả), KHÔNG chứa logic nghiệp vụ.
 *
 * POST   /api/products        -> tạo mới
 * GET    /api/products        -> danh sách
 * GET    /api/products/{id}   -> chi tiết
 * PUT    /api/products/{id}   -> cập nhật
 * DELETE /api/products/{id}   -> xóa mềm
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // @Valid kích hoạt kiểm tra dữ liệu trong ProductRequest; @RequestBody đọc JSON body.
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(productService.getAll()); // 200
    }

    // @PathVariable lấy {id} từ URL.
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Integer id,
                                                  @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        productService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}