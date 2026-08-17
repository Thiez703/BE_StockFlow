package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.InboundDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface InboundDetailRepository extends JpaRepository<InboundDetailEntity, Integer> {
    List<InboundDetailEntity> findByInbound_IdIn(List<Integer> inboundIds);

    @Query("SELECT d.unitPrice FROM InboundDetailEntity d " +
           "WHERE d.lot.id = :lotId " +
           "ORDER BY d.inbound.createdAt DESC, d.id DESC")
    List<BigDecimal> findUnitPricesByLotIdOrderByLatest(@Param("lotId") Integer lotId);
}
