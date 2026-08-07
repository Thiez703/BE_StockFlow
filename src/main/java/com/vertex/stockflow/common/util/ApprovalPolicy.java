package com.vertex.stockflow.common.util;

import com.vertex.stockflow.entity.UserEntity;
import com.vertex.stockflow.exception.IllegalOperationException;
import org.springframework.stereotype.Component;

// BR-04 (người duyệt khác người lập) + BR-05 (người duyệt cấp bậc cao hơn) -
// dùng chung cho StocktakeServiceImpl và AbnormalStockServiceImpl, tách ra đây để tránh
// 2 bản logic giống hệt nhau bị lệch nếu sau này BR-05 thay đổi mà chỉ sửa 1 trong 2 nơi.
@Component
public class ApprovalPolicy {

    public void validateApprover(UserEntity creator, UserEntity approver) {
        if (creator.getId().equals(approver.getId())) {
            throw new IllegalOperationException("Người duyệt không được trùng với người lập phiếu");
        }
        if (approver.getRole().getLevel() <= creator.getRole().getLevel()) {
            throw new IllegalOperationException("Người duyệt phải có cấp bậc cao hơn người lập phiếu");
        }
    }
}
