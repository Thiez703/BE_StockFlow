package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.dto.request.StorageLocationCreateRequest;
import com.vertex.stockflow.dto.request.StorageLocationUpdateRequest;
import com.vertex.stockflow.dto.response.StorageLocationResponse;
import com.vertex.stockflow.entity.StorageLocationEntity;
import com.vertex.stockflow.entity.WarehouseEntity;
import com.vertex.stockflow.exception.DuplicateResourceException;
import com.vertex.stockflow.exception.ResourceNotFoundException;
import com.vertex.stockflow.repository.StorageLocationRepository;
import com.vertex.stockflow.repository.WarehouseRepository;
import com.vertex.stockflow.service.StorageLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StorageLocationServiceImpl implements StorageLocationService {

    private static final String[] DEFAULT_LOCATION_CODES = {"A01", "A02", "A03", "A04", "A05"};

    private final StorageLocationRepository storageLocationRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    public StorageLocationResponse create(StorageLocationCreateRequest request) {
        WarehouseEntity warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + request.getWarehouseId()));

        if (storageLocationRepository.existsByWarehouseIdAndLocationCode(warehouse.getId(), request.getLocationCode())) {
            throw new DuplicateResourceException(
                    "Location code '" + request.getLocationCode() + "' already exists in this warehouse");
        }

        StorageLocationEntity entity = StorageLocationEntity.builder()
                .warehouse(warehouse)
                .zoneCode(request.getZoneCode())
                .locationCode(request.getLocationCode())
                .build();

        return toResponse(storageLocationRepository.save(entity));
    }

    @Override
    public StorageLocationResponse update(Integer id, StorageLocationUpdateRequest request) {
        StorageLocationEntity entity = findEntityOrThrow(id);

        if (!entity.getLocationCode().equals(request.getLocationCode())
                && storageLocationRepository.existsByWarehouseIdAndLocationCodeAndIdNot(
                        entity.getWarehouse().getId(), request.getLocationCode(), id)) {
            throw new DuplicateResourceException(
                    "Location code '" + request.getLocationCode() + "' already exists in this warehouse");
        }

        entity.setZoneCode(request.getZoneCode());
        entity.setLocationCode(request.getLocationCode());

        return toResponse(storageLocationRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        StorageLocationEntity entity = findEntityOrThrow(id);
        storageLocationRepository.delete(entity);
    }

    @Override
    public StorageLocationResponse getById(Integer id) {
        return toResponse(findEntityOrThrow(id));
    }

    @Override
    public List<StorageLocationResponse> getByWarehouseId(Integer warehouseId) {
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new ResourceNotFoundException("Warehouse not found with id: " + warehouseId);
        }
        return storageLocationRepository.findByWarehouseId(warehouseId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<StorageLocationResponse> getAll() {
        return storageLocationRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void createDefaultLocations(WarehouseEntity warehouse) {
        for (String code : DEFAULT_LOCATION_CODES) {
            StorageLocationEntity location = StorageLocationEntity.builder()
                    .warehouse(warehouse)
                    .locationCode(code)
                    .build();
            storageLocationRepository.save(location);
        }
    }

    private StorageLocationEntity findEntityOrThrow(Integer id) {
        return storageLocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Storage location not found with id: " + id));
    }

    private StorageLocationResponse toResponse(StorageLocationEntity entity) {
        return new StorageLocationResponse(
                entity.getId(),
                entity.getWarehouse().getId(),
                entity.getZoneCode(),
                entity.getLocationCode(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
