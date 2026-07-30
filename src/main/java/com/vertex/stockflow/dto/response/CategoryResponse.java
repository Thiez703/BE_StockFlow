package com.vertex.stockflow.dto.response;

import com.vertex.stockflow.common.enums.StatusEnum;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Integer id;
    private String name;

    // Chỉ trả parentId (không trả nguyên object CategoryEntity cha) để tránh vòng lặp
    // serialize JSON cha-con-cha-... vô hạn và giảm payload không cần thiết.
    private Integer parentId;

    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Chỉ trả email người tạo/sửa (không trả nguyên UserEntity) - cùng lý do với parentId ở trên.
    private String createdBy;
    private String updatedBy;

    // Danh sách con, chỉ được set khi trả về dạng cây (API /tree).
    // Với các API khác (list phẳng, get by id) field này để null để tránh load thừa dữ liệu.
    private List<CategoryResponse> children;
}
