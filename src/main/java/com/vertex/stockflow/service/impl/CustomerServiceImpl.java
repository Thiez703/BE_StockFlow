package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.StatusEnum;
import com.vertex.stockflow.dto.response.PageReponse;
import com.vertex.stockflow.dto.request.CustomerRequest;
import com.vertex.stockflow.dto.response.CustomerReponse;
import com.vertex.stockflow.entity.CustomerEntity;
import com.vertex.stockflow.exception.CustomerException.DuplicateResourceException;
import com.vertex.stockflow.exception.CustomerException.ResourceNotFoundException;
import com.vertex.stockflow.mapper.CustomerMapper;
import com.vertex.stockflow.repository.CustomerRepository;
import com.vertex.stockflow.service.CustomerService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public CustomerReponse create(CustomerRequest request){
        validateDuplicatePhone(request.getPhone(), null);

        CustomerEntity entity = customerMapper.toEntity(request);
        CustomerEntity saved = customerRepository.save(entity);
        return customerMapper.toReponse(saved);
    }
    @Override
    @Transactional
    public CustomerReponse update(Integer id, CustomerRequest request){
        CustomerEntity entity = findEntityById(id);
        validateDuplicatePhone(request.getPhone(), id);

        customerMapper.updateEntity(entity, request);
        return customerMapper.toReponse(entity);
    }
    @Override
    @Transactional(readOnly = true)
    public CustomerReponse getById(Integer id){
        return customerMapper.toReponse(findEntityById(id));
    }
    @Override
    @Transactional(readOnly = true)
    public PageReponse<CustomerReponse> search(String keyword, StatusEnum status, Pageable pageable){
        Specification<CustomerEntity> spec = Specification
                .where(hasKeyword(keyword))
                .and(hasStatus(status));

        Page<CustomerReponse> page = customerRepository.findAll(spec, pageable)
                .map(customerMapper::toReponse);

        return PageReponse.from(page);
    }
    @Override
    @Transactional
    public CustomerReponse delete(Integer id){
        CustomerEntity entity = findEntityById(id);
        entity.setStatus(StatusEnum.INACTIVE);
        return customerMapper.toReponse(entity);
    }
    private Specification<CustomerEntity> hasKeyword(String keyword){
        if (!StringUtils.hasText(keyword)){
            return null;
        }
        String pattern = "%" + keyword.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(root.get("phone"), pattern)
        );
    }
    private Specification<CustomerEntity> hasStatus(StatusEnum status){
        if (status == null){
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }
    private CustomerEntity findEntityById(Integer id){
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
    }
    private void validateDuplicatePhone(String phone, Integer excludeId){
        if (!StringUtils.hasText(phone)){
            return;
        }
        boolean duplicateed = (excludeId == null)
                ? customerRepository.existsByPhone(phone)
                :customerRepository.existsByPhoneAndIdNot(phone, excludeId);

        if (duplicateed){
            throw new DuplicateResourceException("Customer", "phone", phone);
        }
    }
}
