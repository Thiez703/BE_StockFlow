package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.StatusEnum;
import com.vertex.stockflow.dto.request.SupplierRequest;
import com.vertex.stockflow.dto.response.SupplierResponse;
import com.vertex.stockflow.entity.SupplierEntity;
import com.vertex.stockflow.exception.DuplicateResourceException;
import com.vertex.stockflow.exception.ResourceNotFoundException;
import com.vertex.stockflow.mapper.SupplierMapper;
import com.vertex.stockflow.repository.SupplierRepository;
import com.vertex.stockflow.service.SupplierService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    @Transactional
    public SupplierResponse create(SupplierRequest request) {
        if (supplierRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Supplier code already exists: " + request.getCode());
        }
        if (StringUtils.hasText(request.getPhone()) && supplierRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Supplier phone already exists: " + request.getPhone());
        }
        if (StringUtils.hasText(request.getTaxCode()) && supplierRepository.existsByTaxCode(request.getTaxCode())) {
            throw new DuplicateResourceException("Supplier taxCode already exists: " + request.getTaxCode());
        }

        SupplierEntity entity = SupplierEntity.builder()
                .code(request.getCode())
                .name(request.getName())
                .taxCode(request.getTaxCode())
                .contactPerson(request.getContactPerson())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .note(request.getNote())
                .build();
        return supplierMapper.toResponse(supplierRepository.save(entity));
    }

    @Override
    @Transactional
    public SupplierResponse update(Integer id, SupplierRequest request) {
        SupplierEntity entity = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));

        if (StringUtils.hasText(request.getPhone()) && supplierRepository.existsByPhoneAndIdNot(request.getPhone(), id)) {
            throw new DuplicateResourceException("Supplier phone already exists: " + request.getPhone());
        }
        if (StringUtils.hasText(request.getTaxCode()) && supplierRepository.existsByTaxCodeAndIdNot(request.getTaxCode(), id)) {
            throw new DuplicateResourceException("Supplier taxCode already exists: " + request.getTaxCode());
        }

        entity.setName(request.getName());
        entity.setTaxCode(request.getTaxCode());
        entity.setContactPerson(request.getContactPerson());
        entity.setPhone(request.getPhone());
        entity.setEmail(request.getEmail());
        entity.setAddress(request.getAddress());
        entity.setNote(request.getNote());
        return supplierMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse getById(Integer id) {
        SupplierEntity entity = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        return supplierMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> getAll() {
        return supplierRepository.findAll().stream()
                .map(supplierMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public SupplierResponse delete(Integer id) {
        SupplierEntity entity = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        entity.setStatus(StatusEnum.INACTIVE);
        return supplierMapper.toResponse(entity);
    }
}
