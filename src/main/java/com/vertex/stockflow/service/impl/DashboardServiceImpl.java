package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.dto.response.StorageMapCellResponse;
import com.vertex.stockflow.dto.response.StorageMapRawRow;
import com.vertex.stockflow.dto.response.StorageMapResponse;
import com.vertex.stockflow.dto.response.StorageMapRowResponse;
import com.vertex.stockflow.entity.WarehouseEntity;
import com.vertex.stockflow.exception.ResourceNotFoundException;
import com.vertex.stockflow.mapper.StorageMapMapper;
import com.vertex.stockflow.repository.StorageLocationRepository;
import com.vertex.stockflow.repository.WarehouseRepository;
import com.vertex.stockflow.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final StorageLocationRepository storageLocationRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    public StorageMapResponse getStorageMap(Integer warehouseId) {

        WarehouseEntity warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy thông tin kho hàng. Có thể dữ liệu đã bị xóa."));

        // 1 query duy nhất, trả về flat rows (1 row = 1 cặp location-inventory)
        List<StorageMapRawRow> rawRows =
                storageLocationRepository.findStorageMapByWarehouseId(warehouseId);

        LocalDate today = LocalDate.now();

        // Gom flat rows thành cells với occupants
        List<StorageMapCellResponse> cells = StorageMapMapper.groupIntoCells(rawRows, today);

        // Gom cells theo rowLabel
        Map<String, List<StorageMapCellResponse>> grouped = new LinkedHashMap<>();
        for (StorageMapCellResponse cell : cells) {
            grouped.computeIfAbsent(cell.getRowLabel(), k -> new ArrayList<>()).add(cell);
        }

        List<StorageMapRowResponse> rows = new ArrayList<>();
        for (Map.Entry<String, List<StorageMapCellResponse>> entry : grouped.entrySet()) {
            rows.add(new StorageMapRowResponse(entry.getKey(), entry.getValue()));
        }

        return new StorageMapResponse(warehouse.getId(), warehouse.getCode(), rows);
    }
}
