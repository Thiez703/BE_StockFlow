package com.vertex.stockflow.service;

import com.vertex.stockflow.common.enums.StatusEnum;
import com.vertex.stockflow.dto.response.PageReponse;
import com.vertex.stockflow.dto.request.CustomerRequest;
import com.vertex.stockflow.dto.response.CustomerReponse;

import org.springframework.data.domain.Pageable;

public interface CustomerService {
    CustomerReponse create(CustomerRequest request);
    CustomerReponse update(Integer id, CustomerRequest request);
    CustomerReponse getById(Integer id);
    PageReponse<CustomerReponse> search(String keyword, StatusEnum status, Pageable pageable);
    CustomerReponse delete(Integer id);
}
