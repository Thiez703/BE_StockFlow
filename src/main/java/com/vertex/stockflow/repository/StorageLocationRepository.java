package com.vertex.stockflow.repository;

import com.vertex.stockflow.dto.response.StorageMapRawRow;
import com.vertex.stockflow.entity.StorageLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StorageLocationRepository extends JpaRepository<StorageLocationEntity, Integer> {

    List<StorageLocationEntity> findByWarehouseId(Integer warehouseId);

    boolean existsByWarehouseIdAndLocationCode(Integer warehouseId, String locationCode);

    boolean existsByWarehouseIdAndLocationCodeAndIdNot(Integer warehouseId, String locationCode, Integer id);

    // MỚI: kiểm tra ô (hàng, cột) đã có vị trí nào chiếm chưa - dùng khi CREATE.
    // Spring Data tự sinh câu SQL từ tên method, không cần viết @Query.
    boolean existsByWarehouseIdAndRowLabelAndColIndex(Integer warehouseId, String rowLabel, Integer colIndex);

    // MỚI: như trên nhưng loại trừ chính bản ghi đang sửa - dùng khi UPDATE.
    // Thiếu "AndIdNot" thì sửa vị trí mà giữ nguyên toạ độ cũ sẽ bị báo trùng với chính nó.
    boolean existsByWarehouseIdAndRowLabelAndColIndexAndIdNot(Integer warehouseId, String rowLabel, Integer colIndex, Integer id);

    void deleteByWarehouseId(Integer warehouseId);

    /**
     * Lấy toàn bộ ô của 1 kho trong ĐÚNG 1 câu query (không N+1).
     *
     * LEFT JOIN là mấu chốt: nó giữ lại cả những vị trí không có dòng inventory nào
     * -> đó chính là các ô TRỐNG. Dùng INNER JOIN sẽ mất sạch ô trống.
     *
     * "LEFT JOIN InventoryEntity i ON i.location = sl" là entity join
     * (nối 2 entity không có association khai báo sẵn) - Hibernate 6 hỗ trợ.
     *
     * ORDER BY quyết định thứ tự ô trên lưới, và nhờ nó Service chỉ cần
     * gom nhóm tuần tự là ra đúng thứ tự A->F, 1->6.
     */
    @Query("""
            SELECT new com.vertex.stockflow.dto.response.StorageMapRawRow(
                sl.id, sl.locationCode, sl.rowLabel, sl.colIndex, sl.capacity,
                l.id, l.lotCode, l.expDate,
                p.id, p.code, p.name, p.unit, i.quantity, p.minStock)
            FROM StorageLocationEntity sl
            LEFT JOIN InventoryEntity i ON i.location = sl AND i.quantity > 0
            LEFT JOIN i.lot l
            LEFT JOIN i.product p
            WHERE sl.warehouse.id = :warehouseId
            ORDER BY sl.rowLabel, sl.colIndex
            """)
    List<StorageMapRawRow> findStorageMapByWarehouseId(@Param("warehouseId") Integer warehouseId);
}