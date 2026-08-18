package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.CustomerRequest;
import com.vertex.stockflow.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerService {
    // Tạo khách hàng mới.
    CustomerResponse create(CustomerRequest request);

    // Cập nhật thông tin khách hàng.
    CustomerResponse update(Integer id, CustomerRequest request);

    // Lấy thông tin khách hàng theo ID.
    CustomerResponse getById(Integer id);

    // Lấy danh sách tất cả khách hàng.
    List<CustomerResponse> getAll();

    // Xóa khách hàng.
    void delete(Integer id);

    // Vô hiệu hóa khách hàng.
    CustomerResponse deactivate(Integer id);

    // Kích hoạt khách hàng.
    CustomerResponse activate(Integer id);
}
