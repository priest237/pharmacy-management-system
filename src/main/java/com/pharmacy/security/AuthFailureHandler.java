package com.pharmacy.security;

import com.pharmacy.service.AuditLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {

    private final AuditLogService auditLogService;

    public AuthFailureHandler(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("username");
        auditLogService.logLoginFailure(email, request, exception.getMessage());
        response.sendRedirect(request.getContextPath() + "/login?error");
    }
}
