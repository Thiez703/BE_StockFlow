package com.vertex.stockflow.service;

import com.vertex.stockflow.entity.RefreshTokenEntity;


public interface RefreshTokenService {

    RefreshTokenEntity createRefreshToken(String userEmail);

    RefreshTokenEntity verifyRefreshToken(String token);

    RefreshTokenEntity rotate(RefreshTokenEntity old);

    void deleteByEmail(String email);
}
