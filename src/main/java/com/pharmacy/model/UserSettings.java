package com.pharmacy.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_settings")
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settings_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "email_notifications", nullable = false)
    private boolean emailNotifications = true;

    @Column(name = "order_confirmations", nullable = false)
    private boolean orderConfirmations = true;

    @Column(name = "low_stock_alerts", nullable = false)
    private boolean lowStockAlerts = false; // Admin-specific, but include for users if they want

    @Column(name = "expiry_reminders", nullable = false)
    private boolean expiryReminders = true;

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public boolean isEmailNotifications() { return emailNotifications; }
    public void setEmailNotifications(boolean emailNotifications) { this.emailNotifications = emailNotifications; }

    public boolean isOrderConfirmations() { return orderConfirmations; }
    public void setOrderConfirmations(boolean orderConfirmations) { this.orderConfirmations = orderConfirmations; }

    public boolean isLowStockAlerts() { return lowStockAlerts; }
    public void setLowStockAlerts(boolean lowStockAlerts) { this.lowStockAlerts = lowStockAlerts; }

    public boolean isExpiryReminders() { return expiryReminders; }
    public void setExpiryReminders(boolean expiryReminders) { this.expiryReminders = expiryReminders; }
}