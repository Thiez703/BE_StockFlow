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

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StorageLocationServiceImpl implements StorageLocationService {

    private final StorageLocationRepository storageLocationRepository;
    private final WarehouseRepository warehouseRepository;
    // Số cột mỗi hàng của lưới sơ đồ kho. Tách ra hằng số để sau muốn mở rộng
    // thành 8 cột thì sửa 1 chỗ, không phải dò trong vòng lặp.
    private static final int GRID_COLUMNS = 6;

    // Danh sách nhãn hàng. Dùng List thay vì tính từ ký tự 'A' + i
    // để sau này muốn đặt nhãn không liên tục (A, B, C, X) vẫn được.
    private static final List<String> GRID_ROWS = List.of("A", "B", "C", "D", "E", "F");

    @Override
    public StorageLocationResponse create(StorageLocationCreateRequest request) {

        // Kho phải tồn tại thì mới gắn vị trí vào được
        WarehouseEntity warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Warehouse not found with id: " + request.getWarehouseId()));

        // Kiểm tra theo TOẠ ĐỘ chứ không theo mã.
        // Toạ độ mới là thứ định danh ô trên lưới; mã chỉ là hệ quả của toạ độ.
        if (storageLocationRepository.existsByWarehouseIdAndRowLabelAndColIndex(
                warehouse.getId(), request.getRowLabel(), request.getColIndex())) {
            throw new DuplicateResourceException(
                    "Position " + request.getRowLabel() + "-" + request.getColIndex()
                            + " is already occupied in this warehouse");
        }

        // Sinh mã tại đây, client không được quyết định.
        String locationCode = buildLocationCode(request.getRowLabel(), request.getColIndex());

        StorageLocationEntity entity = StorageLocationEntity.builder()
                .warehouse(warehouse)
                .rowLabel(request.getRowLabel())
                .colIndex(request.getColIndex())
                .locationCode(locationCode)
                .build();

        return toResponse(storageLocationRepository.save(entity));
    }

    @Override
    public StorageLocationResponse update(Integer id, StorageLocationUpdateRequest request) {

        StorageLocationEntity entity = findEntityOrThrow(id);

        // "AndIdNot" loại chính nó ra khỏi phép so trùng,
        // nhờ vậy submit lại y nguyên toạ độ cũ vẫn hợp lệ.
        if (storageLocationRepository.existsByWarehouseIdAndRowLabelAndColIndexAndIdNot(
                entity.getWarehouse().getId(), request.getRowLabel(), request.getColIndex(), id)) {
            throw new DuplicateResourceException(
                    "Position " + request.getRowLabel() + "-" + request.getColIndex()
                            + " is already occupied in this warehouse");
        }

        entity.setRowLabel(request.getRowLabel());
        entity.setColIndex(request.getColIndex());

        // Toạ độ đổi thì mã phải đổi theo, nếu không sẽ lệch:
        // bản ghi nằm ở (B,3) mà mã vẫn ghi "A-01".
        entity.setLocationCode(buildLocationCode(request.getRowLabel(), request.getColIndex()));

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

        // Gom hết vào 1 List rồi save 1 lần ở cuối, thay vì save từng cái trong vòng lặp.
        // 36 lần gọi save() = 36 lần round-trip xuống DB; saveAll() gộp thành batch.
        List<StorageLocationEntity> locations = new ArrayList<>();

        // Vòng ngoài duyệt hàng: A, B, C, D, E, F
        for (String rowLabel : GRID_ROWS) {

            // Vòng trong duyệt cột: 1..6. Bắt đầu từ 1 (không phải 0)
            // vì đây là số người dùng nhìn thấy, không phải index mảng.
            for (int colIndex = 1; colIndex <= GRID_COLUMNS; colIndex++) {

                // Sinh mã hiển thị từ toạ độ: "A" + "-" + "01" -> "A-01".
                // %02d ép về 2 chữ số (1 -> "01") để mã luôn cùng độ dài,
                // nhờ đó sắp xếp theo chuỗi cũng ra đúng thứ tự.
                String locationCode = buildLocationCode(rowLabel, colIndex);

                locations.add(
                        StorageLocationEntity.builder()
                                .warehouse(warehouse)      // gắn vào kho vừa tạo
                                .rowLabel(rowLabel)         // toạ độ hàng - NOT NULL, thiếu là lỗi runtime
                                .colIndex(colIndex)         // toạ độ cột - NOT NULL
                                .locationCode(locationCode) // mã cho người dùng đọc
                                .build()
                );
            }
        }

        // Lưu cả 36 bản ghi trong 1 lần gọi.
        // Đang nằm trong @Transactional của WarehouseServiceImpl.create(),
        // nên nếu bước này lỗi thì kho vừa tạo cũng bị rollback theo - không có kho cụt vị trí.
        storageLocationRepository.saveAll(locations);
    }

    /**
     * Sinh mã vị trí từ toạ độ lưới: ("A", 1) -> "A-01".
     * Tách riêng vì cả create(), update() và createDefaultLocations() đều cần.
     * Sửa quy tắc đặt mã sau này chỉ phải sửa đúng 1 chỗ.
     */
    private static String buildLocationCode(String rowLabel, Integer colIndex) {
        return rowLabel + "-" + String.format("%02d", colIndex);
    }

    private StorageLocationEntity findEntityOrThrow(Integer id) {
        return storageLocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Storage location not found with id: " + id));
    }

    private StorageLocationResponse toResponse(StorageLocationEntity entity) {
        return new StorageLocationResponse(
                entity.getId(),
                entity.getWarehouse().getId(),
                entity.getRowLabel(),      // MỚI
                entity.getColIndex(),      // MỚI
                entity.getLocationCode(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
