package com.vertex.stockflow.config;

import com.vertex.stockflow.entity.AuditLogEntity;
import com.vertex.stockflow.repository.AuditLogRepository;
import com.vertex.stockflow.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuditLogFilter extends OncePerRequestFilter {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        filterChain.doFilter(request, response);

        String method = request.getMethod();
        // Log all modifying requests that succeed
        if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method) || "PATCH".equals(method)) {
            String uri = request.getRequestURI();
            
            // Only log for API endpoints and successful statuses
            if (uri.startsWith("/api/") && response.getStatus() >= 200 && response.getStatus() < 300) {
                // Ignore GET and /api/auth/login, etc. if needed, but logging them as actions is fine.
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                    String username = auth.getName();
                    userRepository.findByEmail(username).ifPresent(user -> {
                        AuditLogEntity log = new AuditLogEntity();
                        log.setUser(user);
                        String entity = "API_CALL";
                        String[] parts = uri.split("/");
                        if (parts.length > 2) {
                            entity = parts[2];
                        }
                        
                        log.setAction("API_" + method);
                        log.setEntityType(entity);
                        log.setDetail("Người dùng thực hiện " + method + " tới " + uri);
                        auditLogRepository.save(log);
                    });
                }
            }
        }
    }
}
