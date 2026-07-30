package com.vertex.stockflow.dto.response;

import com.vertex.stockflow.common.enums.StatusEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierReponse {
    private Integer id;
    private String code;
    private String name;
    private String taxCode;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private String note;
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
