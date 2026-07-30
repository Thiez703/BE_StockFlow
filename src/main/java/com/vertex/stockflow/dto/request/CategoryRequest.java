package com.vertex.stockflow.dto.request;

import com.vertex.stockflow.common.enums.StatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

// Dùng chung cho cả create và update Category vì 2 thao tác này có cùng tập field đầu vào.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    @NotBlank
    // Khớp với giới hạn length = 100 của cột name trong CategoryEntity - chặn ở tầng validate
    // (trả lỗi 400 rõ ràng) thay vì để rơi xuống DB gây lỗi khó hiểu khi tên quá dài.
    @Size(max = 100)
    private String name;

    // Cho phép null: null nghĩa là category gốc (root), không có cha.
    private Integer parentId;

    private StatusEnum status;
}
