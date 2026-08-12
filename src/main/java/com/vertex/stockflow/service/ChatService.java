package com.vertex.stockflow.service;

import com.vertex.stockflow.dto.request.ChatRequest;
import com.vertex.stockflow.dto.response.ConversationResponse;
import com.vertex.stockflow.dto.response.MessageResponse;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface ChatService {
    // Xử lý khi user gửi 1 tin nhắn lên
    MessageResponse processChat(ChatRequest request, User actor);
    
    // Lấy danh sách đoạn chat bên trái
    List<ConversationResponse> getMyConversations(User actor);
    
    // Lấy lịch sử 1 đoạn chat cụ thể
    List<MessageResponse> getConversationMessages(Integer conversationId, User actor);
}
