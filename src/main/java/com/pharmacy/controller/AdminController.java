package com.pharmacy.controller;

import com.pharmacy.model.Medicine;
import com.pharmacy.service.AuditLogService;
import com.pharmacy.service.MedicineService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminController {

    private final AuditLogService auditLogService;
    private final MedicineService medicineService;

    public AdminController(AuditLogService auditLogService, MedicineService medicineService) {
        this.auditLogService = auditLogService;
        this.medicineService = medicineService;
    }

    @GetMapping("/admin/audit-logs")
    public String auditLogs(Model model) {
        model.addAttribute("logs", auditLogService.getRecentLogs());
        model.addAttribute("activePage", "audit-logs");
        return "admin/audit-logs";
    }

    @GetMapping("/admin/medicines")
    public String medicines(Model model) {
        model.addAttribute("medicines", medicineService.getAll());
        model.addAttribute("activePage", "inventory");
        if (!model.containsAttribute("medicine")) {
            model.addAttribute("medicine", new Medicine());
        }
        return "admin/medicines";
    }

    @GetMapping("/admin/medicines/{id}/edit")
    public String editMedicine(@PathVariable Long id, Model model) {
        return medicineService.getById(id)
                .map(medicine -> {
                    model.addAttribute("medicine", medicine);
                    model.addAttribute("medicines", medicineService.getAll());
                    model.addAttribute("activePage", "inventory");
                    model.addAttribute("isEdit", true);
                    return "admin/medicines";
                })
                .orElse("redirect:/admin/medicines");
    }

    @PostMapping("/admin/medicines")
    public String createMedicine(@Valid @ModelAttribute("medicine") Medicine medicine,
                                 BindingResult bindingResult,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("medicines", medicineService.getAll());
            model.addAttribute("activePage", "inventory");
            return "admin/medicines";
        }

        medicineService.save(medicine);
        return "redirect:/admin/medicines";
    }

    @PostMapping("/admin/medicines/{id}")
    public String updateMedicine(@PathVariable Long id,
                                 @Valid @ModelAttribute("medicine") Medicine medicine,
                                 BindingResult bindingResult,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("medicines", medicineService.getAll());
            model.addAttribute("activePage", "inventory");
            model.addAttribute("isEdit", true);
            return "admin/medicines";
        }

        medicineService.update(id, medicine);
        return "redirect:/admin/medicines";
    }

    @PostMapping("/admin/medicines/{id}/delete")
    public String deleteMedicine(@PathVariable Long id) {
        medicineService.deleteById(id);
        return "redirect:/admin/medicines";
    }
}
