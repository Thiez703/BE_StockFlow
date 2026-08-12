package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.StocktakeCreateRequest;
import com.vertex.stockflow.dto.request.StocktakeRejectRequest;
import com.vertex.stockflow.dto.response.StocktakeResponse;
import com.vertex.stockflow.dto.response.StorageMapCellResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface StocktakeService {

    List<StorageMapCellResponse> getInventorySnapshot(Integer warehouseId);

    StocktakeResponse create(StocktakeCreateRequest request, User actor);

    Page<StocktakeResponse> getByWarehouseId(Integer warehouseId, Pageable pageable, User actor);

    StocktakeResponse getById(Integer id);

    StocktakeResponse approve(Integer id, User actor);

    StocktakeResponse reject(Integer id, StocktakeRejectRequest request, User actor);
}
