package com.pharmacy;

import com.pharmacy.model.Medicine;
import com.pharmacy.service.MedicineService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedMedicines(MedicineService medicineService) {
        return args -> {
            if (medicineService.isEmpty()) {
                medicineService.save(createMedicine(
                        "Paracetamol 500mg",
                        "Pain relief and fever reducer tablets.",
                        "/images/medicines/paracetamol.jpg",
                        new BigDecimal("3900.00"),
                        240,
                        LocalDate.now().plusMonths(18)
                ));

                medicineService.save(createMedicine(
                        "Amoxicillin 250mg",
                        "Broad-spectrum antibiotic capsules for bacterial infections.",
                        "/images/medicines/amoxicillin.jpg",
                        new BigDecimal("7200.00"),
                        80,
                        LocalDate.now().plusMonths(10)
                ));

                medicineService.save(createMedicine(
                        "Vitamin C Syrup",
                        "Immune-support syrup suitable for adults and children.",
                        "/images/medicines/vitamin-c-syrup.png",
                        new BigDecimal("5800.00"),
                        56,
                        LocalDate.now().plusMonths(14)
                ));
            }

            syncSampleMedicineImages(medicineService);
        };
    }

    private void syncSampleMedicineImages(MedicineService medicineService) {
        for (Medicine medicine : medicineService.getAll()) {
            boolean changed = false;
            String normalizedName = medicine.getName() == null ? "" : medicine.getName().trim().toLowerCase();

            switch (normalizedName) {
                case "paracetamol 500mg" -> changed = applySampleImage(
                        medicine,
                        "/images/medicines/paracetamol.jpg"
                );
                case "amoxicillin 250mg" -> changed = applySampleImage(
                        medicine,
                        "/images/medicines/amoxicillin.jpg"
                );
                case "vitamin c syrup" -> changed = applySampleImage(
                        medicine,
                        "/images/medicines/vitamin-c-syrup.png"
                );
                default -> {
                }
            }

            if (changed) {
                medicineService.save(medicine);
            }
        }
    }

    private boolean applySampleImage(Medicine medicine, String imageUrl) {
        boolean changed = false;

        if (medicine.getImageUrl() == null || medicine.getImageUrl().isBlank()) {
            medicine.setImageUrl(imageUrl);
            changed = true;
        }

        return changed;
    }

    private Medicine createMedicine(String name, String description, String imageUrl, BigDecimal price,
                                    int stockQuantity, LocalDate expiryDate) {
        Medicine medicine = new Medicine();
        medicine.setName(name);
        medicine.setDescription(description);
        medicine.setImageUrl(imageUrl);
        medicine.setPrice(price);
        medicine.setStockQuantity(stockQuantity);
        medicine.setExpiryDate(expiryDate);
        return medicine;
    }
}
