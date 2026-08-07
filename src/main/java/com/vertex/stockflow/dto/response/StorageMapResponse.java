package com.vertex.stockflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Toàn bộ payload của sơ đồ 1 kho. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorageMapResponse {

    private Integer warehouseId;
    private String warehouseCode;               // để FE hiện tiêu đề "Kho KHO01"
    private List<StorageMapRowResponse> rows;   // đã sắp theo rowLabel A -> F
}