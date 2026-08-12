package com.vertex.stockflow.common.enums;

public enum AuditAction {
    CREATE_USER,
    UPDATE_USER,
    LOCK_USER,
    UNLOCK_USER,
    CHANGE_ROLE,
    RESET_PASSWORD,
    STOCKTAKE_APPROVE,
    STOCKTAKE_REJECT,
    ABNORMAL_STOCK_APPROVE,
    ABNORMAL_STOCK_REJECT,
    CHANGE_PASSWORD,
    INBOUND_CREATE,
    INBOUND_VOID,
    OUTBOUND_CREATE,
    OUTBOUND_VOID
}
//để không gõ tay chuỗi action