package com.vertex.stockflow.controller;

import com.vertex.stockflow.common.enums.StatusEnum;
import com.vertex.stockflow.dto.response.ApiResponse;
import com.vertex.stockflow.dto.response.PageReponse;
import com.vertex.stockflow.dto.request.CustomerRequest;
import com.vertex.stockflow.dto.response.CustomerReponse;
import com.vertex.stockflow.entity.CustomerEntity;
import com.vertex.stockflow.service.CustomerService;
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
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageReponse<CustomerReponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false)StatusEnum status,
            @PageableDefault(size = 8, sort = "id", direction = Sort.Direction.DESC)Pageable pageable){
        PageReponse<CustomerReponse> result = customerService.search(keyword, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerReponse>> getById(@PathVariable Integer id){
        CustomerReponse result = customerService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<CustomerReponse>> create(@Valid @RequestBody CustomerRequest request){
        CustomerReponse result = customerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(result));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<CustomerReponse>> update(@PathVariable Integer id, @Valid @RequestBody CustomerRequest request){
        CustomerReponse result = customerService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật khách hàng thành công", result));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<CustomerReponse>> delete(@PathVariable Integer id){
        CustomerReponse result = customerService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa khách hàng thành công", result));
    }
}
