package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.StatusEnum;
import com.vertex.stockflow.dto.response.PageReponse;
import com.vertex.stockflow.dto.request.SupplierRequest;
import com.vertex.stockflow.dto.response.SupplierReponse;
import com.vertex.stockflow.entity.SupplierEntity;
import com.vertex.stockflow.exception.SupplierException.DuplicateResourceException;
import com.vertex.stockflow.exception.SupplierException.ResourceNotFoundException;
import com.vertex.stockflow.mapper.SupplierMapper;
import com.vertex.stockflow.repository.SupplierRepository;
import com.vertex.stockflow.service.SupplierService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {
    private static final String CODE_PREFIX = "SUP-";

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    @Transactional
    public SupplierReponse create(SupplierRequest request) {
        validateDuplicatePhone(request.getPhone(), null);
        validateDuplicateTaxCode(request.getTaxCode(), null);

        String code = generateNextCode();
        SupplierEntity entity = supplierMapper.toEntity(request, code);
        SupplierEntity saved = supplierRepository.save(entity);
        return supplierMapper.toReponse(saved);
    }

    @Override
    @Transactional
    public SupplierReponse update(Integer id, SupplierRequest request) {
        SupplierEntity entity = findEntityById(id);
        validateDuplicatePhone(request.getPhone(), id);
        validateDuplicateTaxCode(request.getTaxCode(), id);

        supplierMapper.updateEntity(entity, request);
        return supplierMapper.toReponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierReponse getById(Integer id) {
        return supplierMapper.toReponse((findEntityById(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public PageReponse<SupplierReponse> search(String keyword, StatusEnum status, Pageable pageable) {
        Specification<SupplierEntity> spec = Specification
                .where(hasKeyword(keyword))
                .and(hasStatus(status));

        Page<SupplierReponse> page = supplierRepository.findAll(spec, pageable)
                .map(supplierMapper::toReponse);

        return PageReponse.from(page);
    }

    @Override
    @Transactional
    public SupplierReponse delete(Integer id) {
        SupplierEntity entity = findEntityById(id);
        entity.setStatus(StatusEnum.INACTIVE);
        return supplierMapper.toReponse(entity);
    }

    private Specification<SupplierEntity> hasKeyword(String keyword){
        if (!StringUtils.hasText(keyword)){
            return null;
        }
        String pattern = "%" + keyword.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("code")), pattern),
                cb.like(root.get("phone"), pattern)
        );
    }
    private Specification<SupplierEntity> hasStatus(StatusEnum status){
        if (status == null){
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }
    private SupplierEntity findEntityById(Integer id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
    }

    private void validateDuplicatePhone(String phone, Integer excludeId) {
        if (!StringUtils.hasText(phone)) {
            return;
        }
        boolean duplicated = (excludeId == null)
                ? supplierRepository.existsByPhone(phone)
                : supplierRepository.existsByPhoneAndIdNot(phone, excludeId);

        if (duplicated) {
            throw new DuplicateResourceException("Supplier", "phone", phone);
        }
    }

    private void validateDuplicateTaxCode(String taxCode, Integer excludeId) {
        if (!StringUtils.hasText(taxCode)) {
            return;
        }

        boolean duplicated = (excludeId == null)
                ? supplierRepository.existsByTaxCode(taxCode)
                : supplierRepository.existsByTaxCodeAndIdNot(taxCode, excludeId);

        if (duplicated) {
            throw new DuplicateResourceException("Supplier", "taxCode", taxCode);
        }
    }

    private String generateNextCode() {
        int nexSequence = supplierRepository.findTopByOrderByIdDesc()
                .map(last -> {
                    String[] parts = last.getCode().split("-");
                    int lastSequence = Integer.parseInt(parts[parts.length - 1]);
                    return lastSequence + 1;
                })
                .orElse(1);
        return String.format("%s%04d", CODE_PREFIX, nexSequence);
    }
}

