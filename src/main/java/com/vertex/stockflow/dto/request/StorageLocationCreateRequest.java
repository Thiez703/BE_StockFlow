package com.vertex.stockflow.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorageLocationCreateRequest {

    @NotNull
    private Integer warehouseId;

    // ĐÃ XOÁ: private String zoneCode;
    // Cột zone_code không còn tồn tại trong entity nên field này vô nghĩa.
    // Nó chính là lý do compile vẫn SUCCESS: Service không hề đọc tới nó,
    // client gửi lên bao nhiêu cũng bị vứt đi trong im lặng.

    @NotBlank                          // không được null và không được chuỗi rỗng
    @Size(max = 5)                     // khớp length = 5 của cột row_label bên entity
    private String rowLabel;           // nhãn hàng người dùng chọn: "A".."F"

    @NotNull                           // Integer nên dùng @NotNull, không dùng @NotBlank
    @Min(1)                            // cột đánh số từ 1, không có cột 0
    @Max(6)                            // trần cứng theo lưới 6 cột của giao diện
    private Integer colIndex;          // số cột: 1..6

    @Min(1)                            // capacity phải >= 1 nếu có giá trị
    private Integer capacity;          // sức chứa tối đa (Thùng), null = không giới hạn
}