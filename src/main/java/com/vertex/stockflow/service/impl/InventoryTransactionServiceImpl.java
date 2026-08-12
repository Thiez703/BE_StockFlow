package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.RefTypeEnum;
import com.vertex.stockflow.common.specification.InventoryTransactionSpecification;
import com.vertex.stockflow.dto.response.InventoryTransactionResponse;
import com.vertex.stockflow.entity.InventoryTransactionEntity;
import com.vertex.stockflow.mapper.InventoryTransactionMapper;
import com.vertex.stockflow.repository.*;
import com.vertex.stockflow.service.InventoryTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final InboundRepository inboundRepository;
    private final OutboundRepository outboundRepository;
    private final StocktakeRepository stocktakeRepository;
    private final AbnormalStockRepository abnormalStockRepository;

    @Override
    public Page<InventoryTransactionResponse> search(Integer productId, Integer lotId, Integer locationId,
                                                      LocalDateTime from, LocalDateTime to,
                                                      RefTypeEnum refType, Pageable pageable) {
        Specification<InventoryTransactionEntity> spec =
                InventoryTransactionSpecification.filter(productId, lotId, locationId, from, to, refType);
        Page<InventoryTransactionEntity> page = inventoryTransactionRepository.findAll(spec, pageable);
        return mapPage(page);
    }

    @Override
    public Page<InventoryTransactionResponse> getByProduct(Integer productId, Pageable pageable) {
        Specification<InventoryTransactionEntity> spec =
                InventoryTransactionSpecification.filter(productId, null, null, null, null, null);
        Page<InventoryTransactionEntity> page = inventoryTransactionRepository.findAll(spec, pageable);
        return mapPage(page);
    }

    private Page<InventoryTransactionResponse> mapPage(Page<InventoryTransactionEntity> page) {
        Map<RefTypeEnum, Set<Integer>> refIdsByType = new EnumMap<>(RefTypeEnum.class);
        for (InventoryTransactionEntity txn : page.getContent()) {
            refIdsByType.computeIfAbsent(txn.getRefType(), k -> new HashSet<>()).add(txn.getRefId());
        }

        Map<String, String> refCodeMap = new HashMap<>();
        resolveRefCodes(refIdsByType, refCodeMap);

        return page.map(txn -> {
            String key = txn.getRefType() + ":" + txn.getRefId();
            return InventoryTransactionMapper.toResponse(txn, refCodeMap.get(key));
        });
    }

    private void resolveRefCodes(Map<RefTypeEnum, Set<Integer>> refIdsByType, Map<String, String> refCodeMap) {
        Set<Integer> inboundIds = new HashSet<>();
        if (refIdsByType.containsKey(RefTypeEnum.INBOUND)) {
            inboundIds.addAll(refIdsByType.get(RefTypeEnum.INBOUND));
        }
        if (refIdsByType.containsKey(RefTypeEnum.INBOUND_VOID)) {
            inboundIds.addAll(refIdsByType.get(RefTypeEnum.INBOUND_VOID));
        }
        if (!inboundIds.isEmpty()) {
            inboundRepository.findAllById(inboundIds).forEach(e -> {
                String code = e.getCode();
                if (refIdsByType.containsKey(RefTypeEnum.INBOUND) &&
                        refIdsByType.get(RefTypeEnum.INBOUND).contains(e.getId())) {
                    refCodeMap.put(RefTypeEnum.INBOUND + ":" + e.getId(), code);
                }
                if (refIdsByType.containsKey(RefTypeEnum.INBOUND_VOID) &&
                        refIdsByType.get(RefTypeEnum.INBOUND_VOID).contains(e.getId())) {
                    refCodeMap.put(RefTypeEnum.INBOUND_VOID + ":" + e.getId(), code);
                }
            });
        }

        Set<Integer> outboundIds = new HashSet<>();
        if (refIdsByType.containsKey(RefTypeEnum.OUTBOUND)) {
            outboundIds.addAll(refIdsByType.get(RefTypeEnum.OUTBOUND));
        }
        if (refIdsByType.containsKey(RefTypeEnum.OUTBOUND_VOID)) {
            outboundIds.addAll(refIdsByType.get(RefTypeEnum.OUTBOUND_VOID));
        }
        if (!outboundIds.isEmpty()) {
            outboundRepository.findAllById(outboundIds).forEach(e -> {
                String code = e.getCode();
                if (refIdsByType.containsKey(RefTypeEnum.OUTBOUND) &&
                        refIdsByType.get(RefTypeEnum.OUTBOUND).contains(e.getId())) {
                    refCodeMap.put(RefTypeEnum.OUTBOUND + ":" + e.getId(), code);
                }
                if (refIdsByType.containsKey(RefTypeEnum.OUTBOUND_VOID) &&
                        refIdsByType.get(RefTypeEnum.OUTBOUND_VOID).contains(e.getId())) {
                    refCodeMap.put(RefTypeEnum.OUTBOUND_VOID + ":" + e.getId(), code);
                }
            });
        }

        Set<Integer> stocktakeIds = refIdsByType.getOrDefault(RefTypeEnum.STOCKTAKE, Collections.emptySet());
        if (!stocktakeIds.isEmpty()) {
            stocktakeRepository.findAllById(stocktakeIds).forEach(e ->
                    refCodeMap.put(RefTypeEnum.STOCKTAKE + ":" + e.getId(), e.getCode()));
        }

        Set<Integer> abnormalIds = refIdsByType.getOrDefault(RefTypeEnum.ABNORMAL, Collections.emptySet());
        if (!abnormalIds.isEmpty()) {
            abnormalStockRepository.findAllById(abnormalIds).forEach(e ->
                    refCodeMap.put(RefTypeEnum.ABNORMAL + ":" + e.getId(), e.getCode()));
        }
    }
}
