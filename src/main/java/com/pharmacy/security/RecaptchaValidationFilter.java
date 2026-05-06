package com.pharmacy.security;

import com.pharmacy.service.AuditLogService;
import com.pharmacy.service.RecaptchaService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RecaptchaValidationFilter extends OncePerRequestFilter {

    private final AuditLogService auditLogService;
    private final RecaptchaService recaptchaService;

    public RecaptchaValidationFilter(AuditLogService auditLogService, RecaptchaService recaptchaService) {
        this.auditLogService = auditLogService;
        this.recaptchaService = recaptchaService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if ("/login".equals(request.getServletPath()) && "POST".equalsIgnoreCase(request.getMethod())) {
            String token = request.getParameter("g-recaptcha-response");
            boolean valid = recaptchaService.isValid(token, request.getRemoteAddr());
            if (!valid) {
                auditLogService.logRecaptchaFailure(request.getParameter("username"), request);
                response.sendRedirect(request.getContextPath() + "/login?captchaError");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
