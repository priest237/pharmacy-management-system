package com.pharmacy.service;

import com.pharmacy.model.User;
import com.pharmacy.model.UserSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserSettingsService userSettingsService;

    public void sendOrderConfirmation(User user, String orderDetails) {
        UserSettings settings = userSettingsService.getOrCreateSettings(user);
        if (settings.isEmailNotifications() && settings.isOrderConfirmations()) {
            sendEmail(user.getEmail(), "Order Confirmation", "Your order has been confirmed.\n" + orderDetails);
        }
    }

    public void sendLowStockAlert(User user, String medicineName) {
        UserSettings settings = userSettingsService.getOrCreateSettings(user);
        if (settings.isEmailNotifications() && settings.isLowStockAlerts()) {
            sendEmail(user.getEmail(), "Low Stock Alert", "The medicine " + medicineName + " is running low on stock.");
        }
    }

    public void sendExpiryReminder(User user, String medicineName, String expiryDate) {
        UserSettings settings = userSettingsService.getOrCreateSettings(user);
        if (settings.isEmailNotifications() && settings.isExpiryReminders()) {
            sendEmail(user.getEmail(), "Expiry Reminder", "The medicine " + medicineName + " is expiring on " + expiryDate + ".");
        }
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}