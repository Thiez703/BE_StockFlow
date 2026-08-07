package com.vertex.stockflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 1 hàng của lưới. BE gom nhóm sẵn để FE map thẳng ra <div> mỗi hàng,
 * khỏi phải groupBy ở client.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorageMapRowResponse {

    private String rowLabel;                    // "A" - FE in ra nhãn bên trái lưới
    private List<StorageMapCellResponse> cells; // 6 ô, đã sắp theo colIndex tăng dần
}
