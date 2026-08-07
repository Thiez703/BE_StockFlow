package com.vertex.stockflow.common.enums;

public enum RoleEnum {
    // level gắn trực tiếp vào từng hằng số, KHÔNG dùng ordinal() để so cấp bậc:
    // thứ tự khai báo ở đây (ADMIN trước, STAFF sau) không phản ánh đúng cấp bậc nghiệp vụ
    // (BR-05: STAFF < ACCOUNTANT < MANAGER < ADMIN), nên phải tách riêng.
    ADMIN(4),
    MANAGER(3),
    ACCOUNTANT(2),
    STAFF(1);

    private final int level;

    RoleEnum(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
