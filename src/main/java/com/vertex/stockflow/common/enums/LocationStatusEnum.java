package com.vertex.stockflow.common.enums;

/**
 * Trạng thái hiển thị của 1 ô trên sơ đồ kho.
 * BE chỉ trả enum này, việc quy ra màu là của FE.
 */

public enum LocationStatusEnum {
        EMPTY,        // vị trí có thật nhưng chưa có lô nào -> FE vẽ ô nét đứt, chữ "TRỐNG"
        NORMAL,       // đang lưu trữ bình thường -> xanh lá
        BELOW_MIN,    // tồn kho thấp hơn định mức tối thiểu -> hồng
        NEAR_EXPIRY,  // lô còn hạn nhưng sắp hết (trong vòng 30 ngày) -> cam
        EXPIRED       // lô đã quá hạn -> đỏ đậm (FE cần bổ sung mục chú thích này)
    }
