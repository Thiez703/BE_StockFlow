package com.vertex.stockflow.mapper;
import com.vertex.stockflow.dto.response.AuditLogResponse;
import com.vertex.stockflow.entity.AuditLogEntity;
import org.springframework.stereotype.Component;
//Cùng khuôn mẫu với UserMapper đã viết — viết tay, không MapStruct.
@Component
public class AuditLogMapper {
    public AuditLogResponse toResponse(AuditLogEntity entity) {
        AuditLogResponse res = new AuditLogResponse();
        res.setId(entity.getId());
        res.setActorEmail(entity.getUser().getEmail());
        res.setActorFullName(entity.getUser().getFullName());
        res.setAction(entity.getAction());
        res.setEntityType(entity.getEntityType());
        res.setEntityId(entity.getEntityId());
        res.setDetail(entity.getDetail());
        res.setCreatedAt(entity.getCreatedAt());
        return res;
    }
}
