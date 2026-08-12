package com.vertex.stockflow.service.impl;

import com.vertex.stockflow.dto.request.ChatRequest;
import com.vertex.stockflow.dto.response.ConversationResponse;
import com.vertex.stockflow.dto.response.MessageResponse;
import com.vertex.stockflow.entity.ConversationEntity;
import com.vertex.stockflow.entity.MessageEntity;
import com.vertex.stockflow.entity.UserEntity;
import com.vertex.stockflow.repository.ConversationRepository;
import com.vertex.stockflow.repository.MessageRepository;
import com.vertex.stockflow.repository.UserRepository;
import com.vertex.stockflow.service.ChatService;
import com.vertex.stockflow.service.WarehouseAssistant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final WarehouseAssistant warehouseAssistant;

    @Override
    @Transactional
    public MessageResponse processChat(ChatRequest request, User actor) {
        UserEntity currentUser = userRepository.findByEmail(actor.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ConversationEntity conversation;

        if (request.getConversationId() != null) {
            conversation = conversationRepository.findById(request.getConversationId())
                    .orElseThrow(() -> new RuntimeException("Conversation not found"));
            if (!conversation.getUser().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Access Denied");
            }
        } else {
            conversation = ConversationEntity.builder()
                    .user(currentUser)
                    .title(request.getMessage().length() > 30
                            ? request.getMessage().substring(0, 30) + "..."
                            : request.getMessage())
                    .build();
            conversation = conversationRepository.save(conversation);
        }

        // Lưu tin nhắn của người dùng
        MessageEntity userMessage = MessageEntity.builder()
                .conversation(conversation)
                .role("USER")
                .content(request.getMessage())
                .build();
        messageRepository.save(userMessage);

        // Gọi AI Agent qua LangChain4j (MemoryId = conversationId để quản lý lịch sử chat)
        String aiReplyContent;
        try {
            aiReplyContent = warehouseAssistant.chat(conversation.getId(), request.getMessage());
        } catch (Exception e) {
            aiReplyContent = "Xin lỗi, đường truyền tới AI đang gặp sự cố. Bạn thử lại sau nhé!";
        }

        // Lưu tin nhắn của AI (ASSISTANT)
        MessageEntity aiMessage = MessageEntity.builder()
                .conversation(conversation)
                .role("ASSISTANT")
                .content(aiReplyContent)
                .build();
        aiMessage = messageRepository.save(aiMessage);

        return MessageResponse.builder()
                .id(aiMessage.getId())
                .conversationId(conversation.getId())
                .role(aiMessage.getRole())
                .content(aiMessage.getContent())
                .createdAt(aiMessage.getCreatedAt())
                .build();
    }

    @Override
    public List<ConversationResponse> getMyConversations(User actor) {
        UserEntity currentUser = userRepository.findByEmail(actor.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return conversationRepository.findAllByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream().map(conv -> ConversationResponse.builder()
                        .id(conv.getId()).title(conv.getTitle()).createdAt(conv.getCreatedAt()).build())
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageResponse> getConversationMessages(Integer conversationId, User actor) {
        UserEntity currentUser = userRepository.findByEmail(actor.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        ConversationEntity conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        if (!conversation.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access Denied");
        }
        return messageRepository.findAllByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream().map(msg -> MessageResponse.builder()
                        .id(msg.getId()).conversationId(msg.getConversation().getId())
                        .role(msg.getRole()).content(msg.getContent()).createdAt(msg.getCreatedAt()).build())
                .collect(Collectors.toList());
    }
}
