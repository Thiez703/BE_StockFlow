package com.vertex.stockflow.service;

import com.vertex.stockflow.common.enums.AuditAction;
import com.vertex.stockflow.dto.response.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;

public interface AuditLogService {
    void log(User actor, AuditAction action, String entityType, Integer entityId, String detail);

    Page<AuditLogResponse> search(Integer userId, String action, LocalDateTime from, LocalDateTime to, Pageable pageable);
}