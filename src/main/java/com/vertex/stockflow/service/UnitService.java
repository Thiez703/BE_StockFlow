package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.UnitRequest;
import com.vertex.stockflow.dto.response.UnitResponse;

import java.util.List;

public interface UnitService {

    UnitResponse create(UnitRequest request);

    UnitResponse update(Integer id, UnitRequest request);

    UnitResponse getById(Integer id);

    List<UnitResponse> getAll();

    void deactivate(Integer id);

    void activate(Integer id);
}
