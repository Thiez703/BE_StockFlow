package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.dto.request.WarehouseCreateRequest;
import com.vertex.stockflow.dto.request.WarehouseUpdateRequest;
import com.vertex.stockflow.dto.response.WarehouseResponse;
import com.vertex.stockflow.entity.WarehouseEntity;
import com.vertex.stockflow.exception.DuplicateResourceException;
import com.vertex.stockflow.exception.ResourceNotFoundException;
import com.vertex.stockflow.repository.StorageLocationRepository;
import com.vertex.stockflow.repository.WarehouseRepository;
import com.vertex.stockflow.service.StorageLocationService;
import com.vertex.stockflow.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final StorageLocationService storageLocationService;

    @Override
    public WarehouseResponse create(WarehouseCreateRequest request) {
        if (warehouseRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Warehouse code '" + request.getCode() + "' already exists");
        }

        WarehouseEntity warehouse = WarehouseEntity.builder()
                .code(request.getCode())
                .name(request.getName())
                .address(request.getAddress())
                .build();

        WarehouseEntity saved = warehouseRepository.save(warehouse);

        storageLocationService.createDefaultLocations(saved);

        return toResponse(saved);
    }

    @Override
    public WarehouseResponse update(Integer id, WarehouseUpdateRequest request) {
        WarehouseEntity warehouse = findEntityOrThrow(id);
        warehouse.setName(request.getName());
        warehouse.setAddress(request.getAddress());
        return toResponse(warehouseRepository.save(warehouse));
    }

    @Override
    public void delete(Integer id) {
        WarehouseEntity warehouse = findEntityOrThrow(id);
        // Xóa toàn bộ storage location (kể cả DEFAULT) thuộc kho trước để tránh vi phạm khóa ngoại,
        // vì entity không khai báo cascade và không được phép sửa entity.
        storageLocationRepository.deleteByWarehouseId(warehouse.getId());
        warehouseRepository.delete(warehouse);
    }

    @Override
    public WarehouseResponse getById(Integer id) {
        return toResponse(findEntityOrThrow(id));
    }

    @Override
    public List<WarehouseResponse> getAll() {
        return warehouseRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private WarehouseEntity findEntityOrThrow(Integer id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));
    }

    private WarehouseResponse toResponse(WarehouseEntity entity) {
        return new WarehouseResponse(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getAddress(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
