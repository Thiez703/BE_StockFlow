package com.vertex.stockflow.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorageLocationResponse {

    private Integer id;

    private Integer warehouseId;

    // 2 field MỚI - phải đặt ĐÚNG THỨ TỰ này.
    // @AllArgsConstructor sinh constructor theo thứ tự khai báo field từ trên xuống,
    // nên thứ tự ở đây phải khớp y hệt thứ tự tham số trong toResponse().
    private String rowLabel;      // nhãn hàng: "A".."F"
    private Integer colIndex;     // số cột: 1..6

    private String locationCode;  // mã sinh từ toạ độ: "A-01"

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}