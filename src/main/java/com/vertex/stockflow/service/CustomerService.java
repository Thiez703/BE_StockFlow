package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.CustomerRequest;
import com.vertex.stockflow.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerService {
    CustomerResponse create(CustomerRequest request);
    CustomerResponse update(Integer id, CustomerRequest request);
    CustomerResponse getById(Integer id);
    List<CustomerResponse> getAll();
    void delete(Integer id);
    CustomerResponse deactivate(Integer id);
    CustomerResponse activate(Integer id);
}
