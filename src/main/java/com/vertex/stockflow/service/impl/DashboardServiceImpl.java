package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.dto.response.StorageMapCellResponse;
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
@Transactional(readOnly = true)   // readOnly: chỉ đọc, Hibernate bỏ qua dirty checking -> nhẹ hơn
public class DashboardServiceImpl implements DashboardService {

    private final StorageLocationRepository storageLocationRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    public StorageMapResponse getStorageMap(Integer warehouseId) {

        // Kho không tồn tại thì trả 404 rõ ràng,
        // thay vì trả về sơ đồ rỗng khiến FE tưởng kho chưa có vị trí nào.
        WarehouseEntity warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Warehouse not found with id: " + warehouseId));

        // 1 query duy nhất, đã sắp sẵn theo rowLabel rồi colIndex.
        List<StorageMapCellResponse> cells =
                storageLocationRepository.findStorageMapByWarehouseId(warehouseId);

        // Chốt 1 mốc ngày dùng chung cho cả 36 ô.
        // Nếu để mapper tự gọi now(), chạy đúng lúc nửa đêm sẽ có ô tính theo hôm nay, ô theo hôm qua.
        LocalDate today = LocalDate.now();

        // LinkedHashMap giữ nguyên thứ tự chèn -> vì query đã ORDER BY rowLabel,
        // các hàng sẽ tự nằm đúng thứ tự A, B, C, D, E, F mà không cần sort lại.
        Map<String, List<StorageMapCellResponse>> grouped = new LinkedHashMap<>();

        for (StorageMapCellResponse cell : cells) {
            StorageMapMapper.enrich(cell, today);   // tính status + daysToExpiry

            // computeIfAbsent: chưa có hàng này thì tạo list mới, có rồi thì lấy list cũ.
            grouped.computeIfAbsent(cell.getRowLabel(), k -> new ArrayList<>()).add(cell);
        }

        // Đổi Map thành List<Row> cho đúng shape API đã thống nhất.
        List<StorageMapRowResponse> rows = new ArrayList<>();
        for (Map.Entry<String, List<StorageMapCellResponse>> entry : grouped.entrySet()) {
            rows.add(new StorageMapRowResponse(entry.getKey(), entry.getValue()));
        }

        return new StorageMapResponse(warehouse.getId(), warehouse.getCode(), rows);
    }
}