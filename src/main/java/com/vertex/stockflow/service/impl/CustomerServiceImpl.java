package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.StatusEnum;
import com.vertex.stockflow.dto.request.CustomerRequest;
import com.vertex.stockflow.dto.response.CustomerResponse;
import com.vertex.stockflow.entity.CustomerEntity;
import com.vertex.stockflow.exception.DuplicateResourceException;
import com.vertex.stockflow.exception.ResourceNotFoundException;
import com.vertex.stockflow.mapper.CustomerMapper;
import com.vertex.stockflow.repository.CustomerRepository;
import com.vertex.stockflow.service.CustomerService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        if (customerRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Số điện thoại " + request.getPhone() + " đã được sử dụng cho một khách hàng khác.");
        }

        CustomerEntity entity = CustomerEntity.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();
        return customerMapper.toResponse(customerRepository.save(entity));
    }

    @Override
    @Transactional
    public CustomerResponse update(Integer id, CustomerRequest request) {
        CustomerEntity entity = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin khách hàng. Có thể dữ liệu đã bị xóa."));

        if (customerRepository.existsByPhoneAndIdNot(request.getPhone(), id)) {
            throw new DuplicateResourceException("Số điện thoại " + request.getPhone() + " đã được sử dụng cho một khách hàng khác.");
        }

        entity.setName(request.getName());
        entity.setPhone(request.getPhone());
        entity.setAddress(request.getAddress());
        return customerMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getById(Integer id) {
        CustomerEntity entity = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin khách hàng. Có thể dữ liệu đã bị xóa."));
        return customerMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAll() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        CustomerEntity entity = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin khách hàng. Có thể dữ liệu đã bị xóa."));
        customerRepository.delete(entity);
    }

    @Override
    @Transactional
    public CustomerResponse deactivate(Integer id) {
        CustomerEntity entity = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin khách hàng. Có thể dữ liệu đã bị xóa."));
        entity.setStatus(StatusEnum.INACTIVE);
        return customerMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public CustomerResponse activate(Integer id) {
        CustomerEntity entity = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin khách hàng. Có thể dữ liệu đã bị xóa."));
        entity.setStatus(StatusEnum.ACTIVE);
        return customerMapper.toResponse(entity);
    }
}
