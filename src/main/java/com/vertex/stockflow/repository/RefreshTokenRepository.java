package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.RefreshTokenEntity;
import com.vertex.stockflow.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;


public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Integer> {
    Optional<RefreshTokenEntity> findByToken(String token);
    @Modifying
    void deleteByUser(UserEntity user);
}