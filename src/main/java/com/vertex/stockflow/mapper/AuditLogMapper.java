package com.vertex.stockflow.mapper;
import com.vertex.stockflow.dto.response.AuditLogResponse;
import com.vertex.stockflow.entity.AuditLogEntity;
import org.springframework.stereotype.Component;
//Cùng khuôn mẫu với UserMapper đã viết — viết tay, không MapStruct.
@Component
public class AuditLogMapper {
    public AuditLogResponse toResponse(AuditLogEntity entity) {
        AuditLogResponse response = new AuditLogResponse();
        response.setId(entity.getId());
        response.setActorEmail(entity.getUser().getEmail());
        response.setActorFullName(entity.getUser().getFullName());
        response.setAction(entity.getAction());
        response.setEntityType(entity.getEntityType());
        response.setEntityId(entity.getEntityId());
        response.setDetail(entity.getDetail());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }
}
