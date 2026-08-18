package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.vertex.stockflow.common.enums.RoleEnum;
import java.util.List;

import java.util.Optional;
//Tương tác với db
public interface UserRepository extends JpaRepository<UserEntity, Integer>, JpaSpecificationExecutor<UserEntity> {
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    List<UserEntity> findByRole(RoleEnum role);
}
