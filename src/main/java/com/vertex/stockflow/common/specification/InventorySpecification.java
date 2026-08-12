package com.vertex.stockflow.common.specification;

import com.vertex.stockflow.entity.InventoryEntity;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class InventorySpecification {

    public static Specification<InventoryEntity> filter(
            Integer productId, Integer lotId, Integer locationId) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Fetch join only for non-count queries to avoid N+1
            if (Long.class != query.getResultType()) {
                root.fetch("warehouse", JoinType.INNER);
                root.fetch("product", JoinType.INNER);
                root.fetch("lot", JoinType.INNER);
                root.fetch("location", JoinType.INNER);
            }

            if (productId != null) {
                predicates.add(cb.equal(root.get("product").get("id"), productId));
            }
            if (lotId != null) {
                predicates.add(cb.equal(root.get("lot").get("id"), lotId));
            }
            if (locationId != null) {
                predicates.add(cb.equal(root.get("location").get("id"), locationId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
