package com.vertex.stockflow.common.specification;

import com.vertex.stockflow.common.enums.RefTypeEnum;
import com.vertex.stockflow.entity.InventoryTransactionEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InventoryTransactionSpecification {

    public static Specification<InventoryTransactionEntity> filter(
            Integer productId, Integer lotId, Integer locationId,
            LocalDateTime from, LocalDateTime to, RefTypeEnum refType) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (productId != null) {
                predicates.add(cb.equal(root.get("inventory").get("product").get("id"), productId));
            }
            if (lotId != null) {
                predicates.add(cb.equal(root.get("inventory").get("lot").get("id"), lotId));
            }
            if (locationId != null) {
                predicates.add(cb.equal(root.get("inventory").get("location").get("id"), locationId));
            }
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), to));
            }
            if (refType != null) {
                predicates.add(cb.equal(root.get("refType"), refType));
            }

            query.orderBy(cb.desc(root.get("createdAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
