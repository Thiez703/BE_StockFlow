package com.vertex.stockflow.controller;

import com.vertex.stockflow.common.enums.StatusEnum;
import com.vertex.stockflow.dto.response.ApiResponse;
import com.vertex.stockflow.dto.response.PageReponse;
import com.vertex.stockflow.dto.request.SupplierRequest;
import com.vertex.stockflow.dto.response.SupplierReponse;
import com.vertex.stockflow.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierContrller {
    private final SupplierService supplierService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageReponse<SupplierReponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false)StatusEnum status,
            @PageableDefault(size = 8, sort = "id", direction = Sort.Direction.DESC)Pageable pageable){
        PageReponse<SupplierReponse> result = supplierService.search(keyword, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierReponse>> getById(@PathVariable Integer id){
        SupplierReponse result = supplierService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<SupplierReponse>> create(@Valid @RequestBody SupplierRequest request){
        SupplierReponse result = supplierService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(result));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<SupplierReponse>> update(@PathVariable Integer id, @Valid @RequestBody SupplierRequest request){
        SupplierReponse result = supplierService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật nhà cung cấp thành công", result));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<SupplierReponse>> delete(@PathVariable Integer id){
        SupplierReponse result = supplierService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa nhà cung cấp thành công", result));
    }
}
