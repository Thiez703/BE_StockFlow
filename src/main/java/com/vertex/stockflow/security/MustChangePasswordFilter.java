package com.vertex.stockflow.security;

import com.vertex.stockflow.entity.UserEntity;
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
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MustChangePasswordFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    private static final Set<String> WHITELISTED_PATHS = Set.of(
            "/api/auth/change-password",
            "/api/auth/logout",
            "/api/auth/refresh",
            "/api/auth/me"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {

            String path = request.getRequestURI();
            if (!WHITELISTED_PATHS.contains(path)) {
                String email = authentication.getName();
                UserEntity user = userRepository.findByEmail(email).orElse(null);

                if (user != null && Boolean.TRUE.equals(user.getMustChangePassword())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                            "{\"status\":403,\"errorCode\":\"MUST_CHANGE_PASSWORD\",\"message\":\"Bạn phải đổi mật khẩu trước khi tiếp tục sử dụng hệ thống\"}"
                    );
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
