package com.pharmacy.controller;

import com.pharmacy.model.User;
import com.pharmacy.model.UserSettings;
import com.pharmacy.service.AuditLogService;
import com.pharmacy.service.MedicineService;
import com.pharmacy.service.UserService;
import com.pharmacy.service.UserSettingsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private final AuditLogService auditLogService;
    private final MedicineService medicineService;
    private final UserService userService;
    private final UserSettingsService userSettingsService;
    private final String recaptchaSiteKey;

    @Autowired
    public UserController(AuditLogService auditLogService, MedicineService medicineService, UserService userService, UserSettingsService userSettingsService,
                          @Value("${recaptcha.site-key}") String recaptchaSiteKey) {
        this.auditLogService = auditLogService;
        this.medicineService = medicineService;
        this.userService = userService;
        this.userSettingsService = userSettingsService;
        this.recaptchaSiteKey = recaptchaSiteKey;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult bindingResult,
                               @RequestParam("confirmPassword") String confirmPassword,
                               HttpServletRequest request,
                               Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        if (userService.emailExists(user.getEmail())) {
            model.addAttribute("error", "Email already registered");
            return "register";
        }

        User savedUser = userService.register(user);
        auditLogService.logRegistration(savedUser, request);
        return "redirect:/login?registered";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);
        return "login";
    }

    @GetMapping("/home")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        populateDashboardModel(model, authentication);
        model.addAttribute("activePage", "dashboard");
        return "dashboard";
    }

    @GetMapping("/medicines")
    public String medicines(Model model, Authentication authentication) {
        model.addAttribute("pageTitle", "Medicine Catalog");
        populateDashboardModel(model, authentication);
        model.addAttribute("activePage", "medicines");
        return "medicines/index";
    }

    @GetMapping("/medicines/{id}")
    public String medicineDetails(@PathVariable Long id, Model model, Authentication authentication) {
        return medicineService.getById(id)
                .map(medicine -> {
                    model.addAttribute("medicine", medicine);
                    model.addAttribute("userName", resolveUserName(authentication));
                    model.addAttribute("role", resolveRole(authentication));
                    model.addAttribute("activePage", "medicines");
                    return "medicines/details";
                })
                .orElse("redirect:/medicines");
    }

    @GetMapping("/settings")
    public String settings(Model model, Authentication authentication) {
        User user = userService.findByEmail(authentication.getName()).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("settings", userSettingsService.getOrCreateSettings(user));
        model.addAttribute("userName", resolveUserName(authentication));
        model.addAttribute("role", resolveRole(authentication));
        model.addAttribute("activePage", "settings");
        return "settings";
    }

    @PostMapping("/settings/profile")
    public String updateProfile(@Valid @ModelAttribute("user") User updatedUser,
                                BindingResult bindingResult,
                                Authentication authentication,
                                Model model) {
        if (bindingResult.hasErrors()) {
            User user = userService.findByEmail(authentication.getName()).orElseThrow();
            model.addAttribute("user", user);
            model.addAttribute("settings", userSettingsService.getOrCreateSettings(user));
            model.addAttribute("userName", resolveUserName(authentication));
            model.addAttribute("role", resolveRole(authentication));
            model.addAttribute("activePage", "settings");
            return "settings";
        }

        User currentUser = userService.findByEmail(authentication.getName()).orElseThrow();
        currentUser.setFullName(updatedUser.getFullName());
        currentUser.setEmail(updatedUser.getEmail());
        currentUser.setPhone(updatedUser.getPhone());
        userService.save(currentUser);

        return "redirect:/settings?updated";
    }

    @PostMapping("/settings/notifications")
    public String updateNotifications(@ModelAttribute("settings") UserSettings updatedSettings,
                                      Authentication authentication) {
        User user = userService.findByEmail(authentication.getName()).orElseThrow();
        UserSettings settings = userSettingsService.getOrCreateSettings(user);
        settings.setEmailNotifications(updatedSettings.isEmailNotifications());
        settings.setOrderConfirmations(updatedSettings.isOrderConfirmations());
        settings.setLowStockAlerts(updatedSettings.isLowStockAlerts());
        settings.setExpiryReminders(updatedSettings.isExpiryReminders());
        userSettingsService.saveSettings(settings);

        return "redirect:/settings?updated";
    }

    @PostMapping("/settings/password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Authentication authentication,
                                 Model model) {
        User user = userService.findByEmail(authentication.getName()).orElseThrow();

        if (!userService.checkPassword(user, currentPassword)) {
            model.addAttribute("passwordError", "Current password is incorrect");
            populateSettingsModel(model, authentication);
            return "settings";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("passwordError", "New passwords do not match");
            populateSettingsModel(model, authentication);
            return "settings";
        }

        userService.updatePassword(user, newPassword);

        return "redirect:/settings?passwordUpdated";
    }

    private void populateDashboardModel(Model model, Authentication authentication) {
        model.addAttribute("userName", resolveUserName(authentication));
        model.addAttribute("role", resolveRole(authentication));
        model.addAttribute("medicines", medicineService.getAll());
    }

    private void populateSettingsModel(Model model, Authentication authentication) {
        User user = userService.findByEmail(authentication.getName()).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("settings", userSettingsService.getOrCreateSettings(user));
        model.addAttribute("userName", resolveUserName(authentication));
        model.addAttribute("role", resolveRole(authentication));
        model.addAttribute("activePage", "settings");
    }

    private String resolveUserName(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "Guest User";
        }

        return userService.findByEmail(authentication.getName())
                .map(User::getFullName)
                .orElse(authentication.getName());
    }

    private String resolveRole(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "USER";
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                return "ADMIN";
            }
        }

        return "USER";
    }

}
