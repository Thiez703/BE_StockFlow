package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
//Tương tác với db
public interface UserRepository extends JpaRepository<UserEntity, Integer>{
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
