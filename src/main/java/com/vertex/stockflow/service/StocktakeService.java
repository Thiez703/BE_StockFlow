package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.StocktakeCreateRequest;
import com.vertex.stockflow.dto.request.StocktakeRejectRequest;
import com.vertex.stockflow.dto.response.StocktakeResponse;
import com.vertex.stockflow.dto.response.StorageMapCellResponse;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface StocktakeService {

    // Khung tồn kho hiện tại của 1 kho để FE dựng form đếm (quyết định #2, Bước 0).
    List<StorageMapCellResponse> getInventorySnapshot(Integer warehouseId);

    StocktakeResponse create(StocktakeCreateRequest request, User actor);

    List<StocktakeResponse> getByWarehouseId(Integer warehouseId);

    StocktakeResponse getById(Integer id);

    StocktakeResponse approve(Integer id, User actor);

    StocktakeResponse reject(Integer id, StocktakeRejectRequest request, User actor);
}
