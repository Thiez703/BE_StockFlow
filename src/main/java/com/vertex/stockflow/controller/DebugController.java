package com.vertex.stockflow.controller;

import com.vertex.stockflow.entity.InventoryEntity;
import com.vertex.stockflow.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class DebugController {
    private final InventoryRepository inventoryRepository;

    @GetMapping("/inventory")
    public List<String> dump() {
        return inventoryRepository.findAll().stream()
                .map(i -> "ID=" + i.getId() + ", Loc=" + (i.getLocation() != null ? i.getLocation().getId() : "null") + ", Qty=" + i.getQuantity() + ", Prod=" + i.getProduct().getId() + ", Lot=" + i.getLot().getId())
                .collect(Collectors.toList());
    }
}
