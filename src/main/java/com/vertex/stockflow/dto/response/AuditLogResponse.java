package com.vertex.stockflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

//lấy từ entity.getUser() (chính admin đã thao tác), để FE không phải gọi thêm API nào khác chỉ để hiển thị tên người thực hiện.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
    private Long id;
    private String actorEmail;
    private String actorFullName;
    private String action;
    private String entityType;
    private Integer entityId;
    private String detail;
    private LocalDateTime createdAt;
}
