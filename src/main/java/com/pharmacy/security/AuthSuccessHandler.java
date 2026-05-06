package com.pharmacy.security;

import com.pharmacy.model.User;
import com.pharmacy.service.AuditLogService;
import com.pharmacy.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    private final AuditLogService auditLogService;
    private final UserService userService;

    public AuthSuccessHandler(AuditLogService auditLogService, UserService userService) {
        this.auditLogService = auditLogService;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String redirectUrl = "/dashboard";
        String email = authentication.getName();
        String roleName = "USER";

        for (GrantedAuthority auth : authentication.getAuthorities()) {
            String role = auth.getAuthority();
            if (role.equals("ROLE_ADMIN")) {
                roleName = "ADMIN";
                break;
            }
        }

        User user = userService.findByEmail(email).orElse(null);
        auditLogService.logLoginSuccess(email, user != null ? user.getRole() : roleName, request);
        response.sendRedirect(redirectUrl);
    }
}
