package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<ConversationEntity, Integer> {
    
    // Tìm tất cả đoạn chat của một User, sắp xếp theo thời gian mới nhất
    List<ConversationEntity> findAllByUserIdOrderByCreatedAtDesc(Integer userId);
}
