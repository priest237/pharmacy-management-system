package com.pharmacy.service;

import com.pharmacy.model.AuditLog;
import com.pharmacy.model.User;
import com.pharmacy.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logRegistration(User user, HttpServletRequest request) {
        save("REGISTERED", user.getEmail(), user.getFullName(), user.getRole(),
                request, "User account created");
    }

    public void logLoginSuccess(String email, String roleName, HttpServletRequest request) {
        save("LOGIN_SUCCESS", email, null, roleName, request, "User authenticated successfully");
    }

    public void logLoginFailure(String email, HttpServletRequest request, String details) {
        save("LOGIN_FAILED", email, null, null, request, details);
    }

    public void logRecaptchaFailure(String email, HttpServletRequest request) {
        save("RECAPTCHA_FAILED", email, null, null, request, "Login blocked because reCAPTCHA validation failed");
    }

    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop100ByOrderByCreatedAtDesc();
    }

    private void save(String eventType, String email, String fullName, String roleName,
                      HttpServletRequest request, String details) {
        AuditLog log = new AuditLog();
        log.setEventType(eventType);
        log.setEmail(email);
        log.setFullName(fullName);
        log.setRoleName(roleName);
        log.setIpAddress(resolveIpAddress(request));
        log.setUserAgent(truncate(request.getHeader("User-Agent"), 500));
        log.setDetails(truncate(details, 1000));
        auditLogRepository.save(log);
    }

    private String resolveIpAddress(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return truncate(forwardedFor.split(",")[0].trim(), 64);
        }
        return truncate(request.getRemoteAddr(), 64);
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}
