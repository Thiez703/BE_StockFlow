package com.vertex.stockflow.dto.response;
import com.vertex.stockflow.common.enums.StatusEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerReponse {
    private Integer id;
    private String name;
    private String phone;
    private String address;
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
