package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.OutboundDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboundDetailRepository extends JpaRepository<OutboundDetailEntity, Integer> {
    List<OutboundDetailEntity> findByOutbound_IdIn(List<Integer> outboundIds);
}