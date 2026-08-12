package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.StatusEnum;
import com.vertex.stockflow.dto.request.LotCreateRequest;
import com.vertex.stockflow.dto.request.LotUpdateRequest;
import com.vertex.stockflow.dto.response.LotResponse;
import com.vertex.stockflow.entity.LotEntity;
import com.vertex.stockflow.entity.ProductEntity;
import com.vertex.stockflow.exception.DuplicateResourceException;
import com.vertex.stockflow.exception.ResourceNotFoundException;
import com.vertex.stockflow.mapper.LotMapper;
import com.vertex.stockflow.repository.LotRepository;
import com.vertex.stockflow.repository.ProductRepository;
import com.vertex.stockflow.service.LotService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LotServiceImpl implements LotService {

    private final LotRepository lotRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public LotResponse create(LotCreateRequest request) {
        ProductEntity product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));
        
        String lotCode = request.getLotCode();
        if (lotCode == null || lotCode.trim().isEmpty()) {
            lotCode = "LOT-" + product.getCode() + "-" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        }

        if (lotRepository.existsByProductIdAndLotCode(request.getProductId(), lotCode)) {
            throw new DuplicateResourceException("Lot code already exists for product id: " + request.getProductId());
        }
        LotEntity lot = LotEntity.builder()
                .product(product)
                .lotCode(lotCode)
                .mfgDate(request.getMfgDate())
                .expDate(request.getExpDate())
                .status(StatusEnum.ACTIVE)
                .build();

        return LotMapper.toResponse(lotRepository.save(lot));
    }

    @Override
    @Transactional
    public LotResponse update(Integer id, LotUpdateRequest request) {
        LotEntity lotEntity = lotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lot not found with id: " + id));

        lotEntity.setMfgDate(request.getMfgDate());
        lotEntity.setExpDate(request.getExpDate());

        return LotMapper.toResponse(lotRepository.save(lotEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public LotResponse getById(Integer id) {
        LotEntity lotEntity = lotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lot not found with id: " + id));
        return LotMapper.toResponse(lotEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LotResponse> getByProductId(Integer productId) {
        return lotRepository.findByProductId(productId).stream()
                .map(LotMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LotResponse> getAll() {
        return lotRepository.findAll().stream()
                .map(LotMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        LotEntity lotEntity = lotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lot not found with id: " + id));
        lotRepository.delete(lotEntity);
    }

    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void updateExpiredLots() {
        List<LotEntity> lots = lotRepository.findByStatusAndExpDateBefore(StatusEnum.ACTIVE, LocalDate.now());
        for (LotEntity lot : lots) {
            lot.setStatus(StatusEnum.EXPIRED);
        }
        lotRepository.saveAll(lots);
    }
}
