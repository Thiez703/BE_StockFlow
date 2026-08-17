package com.vertex.stockflow.mapper;

import com.vertex.stockflow.common.enums.LocationStatusEnum;
import com.vertex.stockflow.dto.response.OccupantResponse;
import com.vertex.stockflow.dto.response.StorageMapCellResponse;
import com.vertex.stockflow.dto.response.StorageMapRawRow;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Static utility: gom nhóm raw rows thành cells với occupants. */
public final class StorageMapMapper {

    private static final int NEAR_EXPIRY_DAYS = 30;

    private StorageMapMapper() {
    }

    /**
     * Gom danh sách flat rows (1 row = 1 cặp location-inventory) thành
     * danh sách cells (1 cell = 1 location + nhiều occupants).
     */
    public static List<StorageMapCellResponse> groupIntoCells(List<StorageMapRawRow> rawRows, LocalDate today) {
        // LinkedHashMap giữ thứ tự chèn → vì query đã ORDER BY rowLabel, colIndex
        Map<Integer, StorageMapCellResponse> cellMap = new LinkedHashMap<>();

        for (StorageMapRawRow row : rawRows) {
            StorageMapCellResponse cell = cellMap.computeIfAbsent(row.getLocationId(), id -> {
                StorageMapCellResponse c = new StorageMapCellResponse();
                c.setLocationId(row.getLocationId());
                c.setLocationCode(row.getLocationCode());
                c.setRowLabel(row.getRowLabel());
                c.setColIndex(row.getColIndex());
                c.setCapacity(row.getCapacity());
                c.setUsedQuantity(0);
                c.setOccupants(new ArrayList<>());
                return c;
            });

            // Nếu row có inventory (lotId != null và quantity > 0), tạo occupant
            if (row.getLotId() != null && row.getQuantity() != null && row.getQuantity() > 0) {
                OccupantResponse occ = new OccupantResponse();
                occ.setLotId(row.getLotId());
                occ.setLotCode(row.getLotCode());
                occ.setExpDate(row.getExpDate());
                occ.setProductId(row.getProductId());
                occ.setProductCode(row.getProductCode());
                occ.setProductName(row.getProductName());
                occ.setUnit(row.getUnit());
                occ.setQuantity(row.getQuantity());
                occ.setMinStock(row.getMinStock());

                enrichOccupantStatus(occ, today);

                cell.getOccupants().add(occ);
                cell.setUsedQuantity(cell.getUsedQuantity() + row.getQuantity());
            }
        }

        // Tính status tổng cho mỗi cell
        for (StorageMapCellResponse cell : cellMap.values()) {
            if (cell.getOccupants().isEmpty()) {
                cell.setStatus(LocationStatusEnum.EMPTY);
            } else {
                cell.setStatus(worstStatus(cell.getOccupants()));
            }
        }

        return new ArrayList<>(cellMap.values());
    }

    /**
     * Tính status + daysToExpiry cho 1 occupant.
     * Thứ tự ưu tiên: EXPIRED > NEAR_EXPIRY > BELOW_MIN > NORMAL.
     */
    private static void enrichOccupantStatus(OccupantResponse occ, LocalDate today) {
        Integer daysToExpiry = null;
        if (occ.getExpDate() != null) {
            daysToExpiry = (int) ChronoUnit.DAYS.between(today, occ.getExpDate());
            occ.setDaysToExpiry(daysToExpiry);
        }

        if (daysToExpiry != null && daysToExpiry < 0) {
            occ.setStatus(LocationStatusEnum.EXPIRED);
        } else if (daysToExpiry != null && daysToExpiry <= NEAR_EXPIRY_DAYS) {
            occ.setStatus(LocationStatusEnum.NEAR_EXPIRY);
        } else if (occ.getMinStock() != null && occ.getQuantity() < occ.getMinStock()) {
            occ.setStatus(LocationStatusEnum.BELOW_MIN);
        } else {
            occ.setStatus(LocationStatusEnum.NORMAL);
        }
    }

    /** Trả về status "xấu nhất" trong danh sách occupant. */
    private static LocationStatusEnum worstStatus(List<OccupantResponse> occupants) {
        // Ưu tiên: EXPIRED > NEAR_EXPIRY > BELOW_MIN > NORMAL
        LocationStatusEnum worst = LocationStatusEnum.NORMAL;
        for (OccupantResponse occ : occupants) {
            if (occ.getStatus() == LocationStatusEnum.EXPIRED) return LocationStatusEnum.EXPIRED;
            if (occ.getStatus() == LocationStatusEnum.NEAR_EXPIRY) worst = LocationStatusEnum.NEAR_EXPIRY;
            else if (occ.getStatus() == LocationStatusEnum.BELOW_MIN && worst != LocationStatusEnum.NEAR_EXPIRY) {
                worst = LocationStatusEnum.BELOW_MIN;
            }
        }
        return worst;
    }
}
