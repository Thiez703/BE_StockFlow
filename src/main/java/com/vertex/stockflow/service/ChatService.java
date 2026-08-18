package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.ChatRequest;
import com.vertex.stockflow.dto.response.ConversationResponse;
import com.vertex.stockflow.dto.response.MessageResponse;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface ChatService {
    // Xử lý khi user gửi một tin nhắn lên.
    MessageResponse processChat(ChatRequest request, User actor);
    
    // Lấy danh sách đoạn chat bên trái (của người dùng).
    List<ConversationResponse> getMyConversations(User actor);
    
    // Lấy lịch sử tin nhắn của một đoạn chat cụ thể.
    List<MessageResponse> getConversationMessages(Integer conversationId, User actor);
    
    // Xoá đoạn chat.
    void deleteConversation(Integer conversationId, User actor);
}
