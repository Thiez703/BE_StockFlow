package com.vertex.stockflow.controller;

import com.vertex.stockflow.dto.request.ChatRequest;
import com.vertex.stockflow.dto.response.ConversationResponse;
import com.vertex.stockflow.dto.response.MessageResponse;
import com.vertex.stockflow.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 1. API gửi tin nhắn (và nhận phản hồi từ AI ngay lập tức)
    @PostMapping
    public ResponseEntity<MessageResponse> chat(
            @Valid @RequestBody ChatRequest request,
            @AuthenticationPrincipal User actor) {
        return ResponseEntity.ok(chatService.processChat(request, actor));
    }

    // 2. API lấy danh sách các đoạn chat bên trái màn hình
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponse>> getMyConversations(
            @AuthenticationPrincipal User actor) {
        return ResponseEntity.ok(chatService.getMyConversations(actor));
    }

    // 3. API lấy chi tiết lịch sử tin nhắn của 1 đoạn chat
    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<List<MessageResponse>> getConversationMessages(
            @PathVariable Integer id,
            @AuthenticationPrincipal User actor) {
        return ResponseEntity.ok(chatService.getConversationMessages(id, actor));
    }

    // 4. API xoá đoạn chat
    @DeleteMapping("/conversations/{id}")
    public ResponseEntity<Void> deleteConversation(
            @PathVariable Integer id,
            @AuthenticationPrincipal User actor) {
        chatService.deleteConversation(id, actor);
        return ResponseEntity.noContent().build();
    }
}
