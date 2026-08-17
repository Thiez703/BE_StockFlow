package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.common.enums.AuditAction;
import com.vertex.stockflow.common.specification.AuditLogSpecification;
import com.vertex.stockflow.dto.response.AuditLogResponse;
import com.vertex.stockflow.entity.AuditLogEntity;
import com.vertex.stockflow.entity.UserEntity;
import com.vertex.stockflow.mapper.AuditLogMapper;
import com.vertex.stockflow.repository.AuditLogRepository;
import com.vertex.stockflow.repository.UserRepository;
import com.vertex.stockflow.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

//"dịch" từ thông tin ít ỏi của người đang đăng nhập (chỉ email + quyền) sang bản ghi đầy đủ trong DB (có id), rồi lưu 1 dòng nhật ký hoạt động.
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    public void log(User actor, AuditAction action, String entityType, Integer entityId, String detail) {
        UserEntity actorEntity = userRepository.findByEmail(actor.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy thông tin người dùng. Phiên đăng nhập có thể đã hết hạn."));

        AuditLogEntity entry = new AuditLogEntity();
        entry.setUser(actorEntity);
        entry.setAction(action.name());
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setDetail(detail);
        auditLogRepository.save(entry);
    }
    @Override
    public Page<AuditLogResponse> search(Integer userId, String action, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        Specification<AuditLogEntity> spec = AuditLogSpecification.filter(userId, action, from, to);
        return auditLogRepository.findAll(spec, pageable)
                .map(auditLogMapper::toResponse);
    }

}