package com.pharmacy.service;

import com.pharmacy.model.Medicine;
import com.pharmacy.repository.MedicineRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public List<Medicine> getAll() {
        return medicineRepository.findAllByOrderByNameAsc();
    }

    public Optional<Medicine> getById(Long id) {
        return medicineRepository.findById(id);
    }

    public boolean isEmpty() {
        return medicineRepository.count() == 0;
    }

    public Medicine save(Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    public Medicine update(Long id, Medicine formMedicine) {
        Medicine existingMedicine = medicineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Medicine not found: " + id));

        existingMedicine.setName(formMedicine.getName());
        existingMedicine.setDescription(formMedicine.getDescription());
        existingMedicine.setImageUrl(formMedicine.getImageUrl());
        existingMedicine.setPrice(formMedicine.getPrice());
        existingMedicine.setStockQuantity(formMedicine.getStockQuantity());
        existingMedicine.setExpiryDate(formMedicine.getExpiryDate());

        return medicineRepository.save(existingMedicine);
    }

    public void deleteById(Long id) {
        medicineRepository.deleteById(id);
    }
}
