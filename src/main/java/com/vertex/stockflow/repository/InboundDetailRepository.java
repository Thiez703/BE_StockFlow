package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.InboundDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InboundDetailRepository extends JpaRepository<InboundDetailEntity, Integer> {
    List<InboundDetailEntity> findByInbound_IdIn(List<Integer> inboundIds);
}
