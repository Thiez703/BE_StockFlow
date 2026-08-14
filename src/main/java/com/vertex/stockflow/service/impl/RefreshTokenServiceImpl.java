package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.entity.RefreshTokenEntity;
import com.vertex.stockflow.entity.UserEntity;
import com.vertex.stockflow.exception.IllegalOperationException;
import com.vertex.stockflow.exception.ResourceNotFoundException;
import com.vertex.stockflow.repository.RefreshTokenRepository;
import com.vertex.stockflow.repository.UserRepository;
import com.vertex.stockflow.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final long REFRESH_TOKEN_EXPIRY_MS = 7 * 24 * 60 * 60 * 1000L;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Override
    public RefreshTokenEntity createRefreshToken(String userEmail) {

        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));


        return refreshTokenRepository.save(
                RefreshTokenEntity.builder()
                        .user(userEntity)
                        .token(java.util.UUID.randomUUID().toString())
                        .expiriesAt(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY_MS)
                        .build());
    }

    @Override
    public RefreshTokenEntity verifyRefreshToken(String token){
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy refresh token"));

        // Nếu thời điểm hết hạn < thời điểm hiện tại → xoa token đã hết hạn
        if (refreshTokenEntity.getExpiriesAt() < System.currentTimeMillis()) {
            refreshTokenRepository.delete(refreshTokenEntity);
            throw new IllegalOperationException("Refresh token đã hết hạn");
        }
        return refreshTokenEntity;
    }

    //mục đích khi người dùng gọi /refresh thì sẽ tạo 1 token mới khi nghi ngờ token bị lộ
    @Override
    public RefreshTokenEntity rotate(RefreshTokenEntity old) {
        refreshTokenRepository.delete(old);
        return createRefreshToken(old.getUser().getEmail());
    }

    // xóa token khi logout
    @Override
    public void deleteByToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshTokenRepository::delete);
    }

}
