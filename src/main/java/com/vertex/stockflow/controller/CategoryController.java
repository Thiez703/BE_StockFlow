package com.vertex.stockflow.controller;

import com.vertex.stockflow.dto.request.CategoryRequest;
import com.vertex.stockflow.dto.response.CategoryResponse;
import com.vertex.stockflow.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // Chỉ ADMIN/MANAGER được ghi dữ liệu (create/update/delete); các API GET vẫn mở
    // cho mọi user đã đăng nhập, không phân biệt role.
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.create(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable Integer id, @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<CategoryResponse> deactivate(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.deactivate(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<CategoryResponse> activate(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.activate(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    // Trả danh sách phẳng (không lồng children) - phù hợp cho dropdown, bảng danh sách.
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    // Trả cấu trúc cây (children lồng nhau) - phù hợp cho hiển thị dạng tree ngoài UI.
    @GetMapping("/tree")
    public ResponseEntity<List<CategoryResponse>> getTree() {
        return ResponseEntity.ok(categoryService.getTree());
    }
}
