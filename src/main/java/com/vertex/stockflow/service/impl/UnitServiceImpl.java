package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.StatusEnum;
import com.vertex.stockflow.dto.request.UnitRequest;
import com.vertex.stockflow.dto.response.UnitResponse;
import com.vertex.stockflow.entity.UnitEntity;
import com.vertex.stockflow.exception.DuplicateResourceException;
import com.vertex.stockflow.exception.ResourceNotFoundException;
import com.vertex.stockflow.mapper.UnitMapper;
import com.vertex.stockflow.repository.UnitRepository;
import com.vertex.stockflow.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;
    private final UnitMapper unitMapper;


    @Override
    public UnitResponse create(UnitRequest request) {
        if (unitRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Đơn vị với mã " + request.getCode() + " đã tồn tại");
        }

        UnitEntity unitEntity = UnitEntity.builder()
                .code(request.getCode())
                .name(request.getName())
                .status(StatusEnum.ACTIVE)
                .build();
        unitEntity = unitRepository.save(unitEntity);
        return unitMapper.toResponse(unitEntity);
    }

    @Override
    public UnitResponse update(Integer id, UnitRequest request) {
        UnitEntity unitEntity = unitRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Đơn vị với id " + id + " không tồn tại"));
        if (!unitEntity.getCode().equals(request.getCode()) && unitRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Đơn vị với mã " + request.getCode() + " đã tồn tại");
        }

        unitEntity.setCode(request.getCode());
        unitEntity.setName(request.getName());
        unitEntity = unitRepository.save(unitEntity);

        return unitMapper.toResponse(unitEntity);
    }

    @Override
    public UnitResponse getById(Integer id) {
        UnitEntity unitEntity = unitRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Đơn vị với id " + id + " không tồn tại"));

        return unitMapper.toResponse(unitEntity);
    }

    @Override
    public List<UnitResponse> getAll() {

        return unitRepository.findAll().stream().map(unitMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public void deactivate(Integer id) {
        UnitEntity unitEntity = unitRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Đơn vị với id " + id + " không tồn tại"));

        unitEntity.setStatus(StatusEnum.INACTIVE);
        unitRepository.save(unitEntity);
    }

    @Override
    public void activate(Integer id) {
        UnitEntity unitEntity = unitRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Đơn vị với id " + id + " không tồn tại"));

        unitEntity.setStatus(StatusEnum.ACTIVE);
        unitRepository.save(unitEntity);
    }
}
