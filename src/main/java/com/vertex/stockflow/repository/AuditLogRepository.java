package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
//thực hiện mọi thao tác với cơ sở dữ liệu
public interface AuditLogRepository extends JpaRepository<AuditLogEntity,Long> {
}
