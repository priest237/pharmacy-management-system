package com.pharmacy.service;

import com.pharmacy.model.User;
import com.pharmacy.model.UserSettings;
import com.pharmacy.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserSettingsService {

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    public Optional<UserSettings> getSettingsByUser(User user) {
        return userSettingsRepository.findByUser(user);
    }

    public UserSettings saveSettings(UserSettings settings) {
        return userSettingsRepository.save(settings);
    }

    public UserSettings createDefaultSettings(User user) {
        UserSettings settings = new UserSettings();
        settings.setUser(user);
        // Defaults are set in the entity
        return saveSettings(settings);
    }

    public UserSettings getOrCreateSettings(User user) {
        return getSettingsByUser(user).orElseGet(() -> createDefaultSettings(user));
    }
}