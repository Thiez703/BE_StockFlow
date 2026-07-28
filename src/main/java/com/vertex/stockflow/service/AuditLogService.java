package com.vertex.stockflow.service;

import com.vertex.stockflow.common.enums.AuditAction;
import org.springframework.security.core.userdetails.User;

public interface AuditLogService {
    void log(User actor, AuditAction action, String entityType, Integer entityId, String detail);
}