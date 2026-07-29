package com.vertex.stockflow.controller;

import com.vertex.stockflow.dto.request.AssignRoleRequest;
import com.vertex.stockflow.dto.request.CreateUserRequest;
import com.vertex.stockflow.dto.request.UpdateUserRequest;
import com.vertex.stockflow.dto.response.UserManagementResponse;
import com.vertex.stockflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    //Tự tìm UserService để gán vào
    //Đây là 1 bean
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    //Post là tạo mới tài nguyên

    @PostMapping
    public ResponseEntity<UserManagementResponse> create(@Valid @RequestBody CreateUserRequest req,
                                               @AuthenticationPrincipal User actor) {
        return ResponseEntity.ok(userService.create(req, actor));
    }
    //Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<UserManagementResponse> update(@PathVariable Integer id,
                                               @Valid @RequestBody UpdateUserRequest req,
                                               @AuthenticationPrincipal User actor) {
        return ResponseEntity.ok(userService.update(id, req, actor));
    }
    //Khóa
    @PatchMapping("/{id}/lock")
    public ResponseEntity<Void> lock(@PathVariable Integer id, @AuthenticationPrincipal User actor) {
        userService.lock(id, actor);
        return ResponseEntity.noContent().build();
    }
    //Mở khóa
    @PatchMapping("/{id}/unlock")
    public ResponseEntity<Void> unlock(@PathVariable Integer id, @AuthenticationPrincipal User actor) {
        userService.unlock(id, actor);
        return ResponseEntity.noContent().build();
    }
    //Phân quyền
    @PatchMapping("/{id}/role")
    public ResponseEntity<UserManagementResponse> assignRole(@PathVariable Integer id,
                                                   @Valid @RequestBody AssignRoleRequest req,
                                                   @AuthenticationPrincipal User actor) {
        return ResponseEntity.ok(userService.assignRole(id, req, actor));
    }
    //Gửi mk về mail
    @PostMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(@PathVariable Integer id, @AuthenticationPrincipal User actor) {
        userService.resetPassword(id, actor);
        return ResponseEntity.noContent().build();
    }


}
