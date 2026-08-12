package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.InboundEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InboundRepository extends JpaRepository<InboundEntity, Integer>,
        JpaSpecificationExecutor<InboundEntity> {
}
