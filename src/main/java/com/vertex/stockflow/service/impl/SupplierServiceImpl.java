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
            throw new DuplicateResourceException("Mã nhà cung cấp " + request.getCode() + " đã tồn tại.");
        }
        if (StringUtils.hasText(request.getPhone()) && supplierRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Số điện thoại " + request.getPhone() + " đã được sử dụng cho một nhà cung cấp khác.");
        }
        if (StringUtils.hasText(request.getTaxCode()) && supplierRepository.existsByTaxCode(request.getTaxCode())) {
            throw new DuplicateResourceException("Mã số thuế " + request.getTaxCode() + " đã được sử dụng cho một nhà cung cấp khác.");
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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin nhà cung cấp. Có thể dữ liệu đã bị xóa."));

        if (StringUtils.hasText(request.getPhone()) && supplierRepository.existsByPhoneAndIdNot(request.getPhone(), id)) {
            throw new DuplicateResourceException("Số điện thoại " + request.getPhone() + " đã được sử dụng cho một nhà cung cấp khác.");
        }
        if (StringUtils.hasText(request.getTaxCode()) && supplierRepository.existsByTaxCodeAndIdNot(request.getTaxCode(), id)) {
            throw new DuplicateResourceException("Mã số thuế " + request.getTaxCode() + " đã được sử dụng cho một nhà cung cấp khác.");
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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin nhà cung cấp. Có thể dữ liệu đã bị xóa."));
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
    public void delete(Integer id) {
        SupplierEntity entity = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin nhà cung cấp. Có thể dữ liệu đã bị xóa."));
        supplierRepository.delete(entity);
    }

    @Override
    @Transactional
    public SupplierResponse deactivate(Integer id) {
        SupplierEntity entity = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin nhà cung cấp. Có thể dữ liệu đã bị xóa."));
        entity.setStatus(StatusEnum.INACTIVE);
        return supplierMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public SupplierResponse activate(Integer id) {
        SupplierEntity entity = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin nhà cung cấp. Có thể dữ liệu đã bị xóa."));
        entity.setStatus(StatusEnum.ACTIVE);
        return supplierMapper.toResponse(entity);
    }
}
