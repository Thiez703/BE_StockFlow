package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.StocktakeCreateRequest;
import com.vertex.stockflow.dto.request.StocktakeRejectRequest;
import com.vertex.stockflow.dto.response.StocktakeResponse;
import com.vertex.stockflow.dto.response.StorageMapRawRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface StocktakeService {
    // Lấy bản chụp tồn kho hiện tại để đối chiếu kiểm kê.
    List<StorageMapRawRow> getInventorySnapshot(Integer warehouseId);

    // Tạo phiếu kiểm kê.
    StocktakeResponse create(StocktakeCreateRequest request, User actor);

    // Lấy danh sách phiếu kiểm kê theo kho.
    Page<StocktakeResponse> getByWarehouseId(Integer warehouseId, Pageable pageable, User actor);

    // Lấy thông tin phiếu kiểm kê theo ID.
    StocktakeResponse getById(Integer id, User actor);

    // Phê duyệt phiếu kiểm kê.
    StocktakeResponse approve(Integer id, User actor);

    // Từ chối phiếu kiểm kê.
    StocktakeResponse reject(Integer id, StocktakeRejectRequest request, User actor);
}
