package com.vertex.stockflow.service;

import com.vertex.stockflow.entity.RefreshTokenEntity;


public interface RefreshTokenService {
    // Tạo refresh token mới cho người dùng.
    RefreshTokenEntity createRefreshToken(String userEmail);

    // Kiểm tra tính hợp lệ của refresh token.
    RefreshTokenEntity verifyRefreshToken(String token);

    // Xoay vòng (làm mới) refresh token cũ.
    RefreshTokenEntity rotate(RefreshTokenEntity old);

    // Xóa refresh token.
    void deleteByToken(String token);
}
