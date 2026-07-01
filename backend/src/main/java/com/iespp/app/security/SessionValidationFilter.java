package com.iespp.app.security;

import com.iespp.app.models.User;
import com.iespp.app.repositories.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Optional;

public class SessionValidationFilter implements Filter {

    private final UserRepository userRepository;

    public SessionValidationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;
        String path = httpReq.getRequestURI();
        String method = httpReq.getMethod();

        // Skip public endpoints
        if (esRutaPublica(method, path)) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpReq.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String sessionHeader = httpReq.getHeader("X-Session-Token");
        if (!StringUtils.hasText(sessionHeader)) {
            chain.doFilter(request, response);
            return;
        }

        // Extract email from JWT (already validated by JwtAuthenticationFilter)
        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            chain.doFilter(request, response);
            return;
        }

        String email = auth.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String dbSessionToken = user.getSessionToken();
            if (dbSessionToken != null && !dbSessionToken.equals(sessionHeader)) {
                httpRes.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpRes.setContentType("application/json;charset=UTF-8");
                httpRes.getWriter().write(
                    "{\"error\":\"sesion_expirada\",\"message\":\"La cuenta ya fue iniciada desde otro dispositivo.\"}"
                );
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean esRutaPublica(String method, String path) {
        if (path.startsWith("/api/auth/")) return true;
        if (path.startsWith("/api/test/")) return true;
        if ("GET".equalsIgnoreCase(method) && path.startsWith("/api/programas-estudio/")) return true;
        if ("GET".equalsIgnoreCase(method) && path.startsWith("/api/cursos/")) return true;
        if ("GET".equalsIgnoreCase(method) && path.startsWith("/api/docentes/")) return true;
        if ("OPTIONS".equalsIgnoreCase(method)) return true;
        return false;
    }
}
