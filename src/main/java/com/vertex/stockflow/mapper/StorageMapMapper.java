package com.vertex.stockflow.mapper;

import com.vertex.stockflow.common.enums.LocationStatusEnum;
import com.vertex.stockflow.dto.response.StorageMapCellResponse;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Static utility, theo đúng convention của LotMapper trong project. */
public final class StorageMapMapper {

    /** Ngưỡng cảnh báo lô sắp hết hạn, tính bằng ngày. */
    private static final int NEAR_EXPIRY_DAYS = 30;

    // Constructor private: chặn việc new class utility này ra.
    private StorageMapMapper() {
    }

    /**
     * Tính daysToExpiry và status cho 1 ô, rồi set ngược vào chính ô đó.
     * Nhận today từ ngoài truyền vào thay vì gọi LocalDate.now() bên trong
     * để 36 ô dùng chung 1 mốc thời gian, và để sau này viết unit test được.
     */
    public static void enrich(StorageMapCellResponse cell, LocalDate today) {

        // quantity == null nghĩa là LEFT JOIN không tìm được dòng inventory nào
        // -> vị trí này đang rảnh. Thoát sớm, khỏi tính tiếp.
        if (cell.getQuantity() == null) {
            cell.setStatus(LocationStatusEnum.EMPTY);
            return;
        }

        // Số ngày còn lại tới hạn. Âm = đã quá hạn.
        // Phòng thủ null cho expDate dù entity khai báo bắt buộc,
        // vì dữ liệu cũ có thể lọt qua trước khi thêm ràng buộc.
        Integer daysToExpiry = null;
        if (cell.getExpDate() != null) {
            daysToExpiry = (int) ChronoUnit.DAYS.between(today, cell.getExpDate());
            cell.setDaysToExpiry(daysToExpiry);
        }

        // Thứ tự if-else dưới đây CHÍNH LÀ độ ưu tiên màu đã chốt:
        // EXPIRED > NEAR_EXPIRY > BELOW_MIN > NORMAL. Đảo thứ tự là sai nghiệp vụ.
        if (daysToExpiry != null && daysToExpiry < 0) {
            cell.setStatus(LocationStatusEnum.EXPIRED);

        } else if (daysToExpiry != null && daysToExpiry <= NEAR_EXPIRY_DAYS) {
            cell.setStatus(LocationStatusEnum.NEAR_EXPIRY);

        } else if (cell.getMinStock() != null && cell.getQuantity() < cell.getMinStock()) {
            // Dùng "<" chứ không "<=": tồn 600 / min 600 là VỪA ĐỦ, chưa phải dưới mức.
            // minStock == null nghĩa là sản phẩm chưa đặt định mức -> không bao giờ cảnh báo.
            cell.setStatus(LocationStatusEnum.BELOW_MIN);

        } else {
            cell.setStatus(LocationStatusEnum.NORMAL);
        }
    }
}