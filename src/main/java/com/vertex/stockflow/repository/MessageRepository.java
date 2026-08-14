package com.vertex.stockflow.repository;

import com.vertex.stockflow.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {

    // Lấy toàn bộ lịch sử tin nhắn của 1 cuộc hội thoại, sắp xếp theo thời gian cũ -> mới (để render từ trên xuống dưới)
    List<MessageEntity> findAllByConversationIdOrderByCreatedAtAsc(Integer conversationId);

    void deleteAllByConversationId(Integer conversationId);
}
