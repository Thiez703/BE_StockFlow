package com.vertex.stockflow.service;

import com.vertex.stockflow.common.enums.AuditAction;
import com.vertex.stockflow.dto.response.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;

public interface AuditLogService {
    // Ghi lại nhật ký hệ thống (audit log).
    void log(User actor, AuditAction action, String entityType, Integer entityId, String detail);

    // Tìm kiếm nhật ký hệ thống theo các tiêu chí.
    Page<AuditLogResponse> search(Integer userId, String action, LocalDateTime from, LocalDateTime to, Pageable pageable);
}