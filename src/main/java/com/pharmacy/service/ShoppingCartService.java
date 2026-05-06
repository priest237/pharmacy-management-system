package com.pharmacy.service;

import com.pharmacy.model.CartItemView;
import com.pharmacy.model.Medicine;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ShoppingCartService {

    private final MedicineService medicineService;
    private final Map<Long, Integer> items = new LinkedHashMap<>();

    public ShoppingCartService(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    public void addItem(Long medicineId, int quantity) {
        if (quantity <= 0) {
            return;
        }

        medicineService.getById(medicineId).ifPresent(medicine -> {
            int existingQuantity = items.getOrDefault(medicineId, 0);
            int cappedQuantity = Math.min(existingQuantity + quantity, medicine.getStockQuantity());

            if (cappedQuantity > 0) {
                items.put(medicineId, cappedQuantity);
            }
        });
    }

    public void updateItem(Long medicineId, int quantity) {
        if (quantity <= 0) {
            items.remove(medicineId);
            return;
        }

        medicineService.getById(medicineId).ifPresent(medicine -> {
            int cappedQuantity = Math.min(quantity, medicine.getStockQuantity());
            if (cappedQuantity > 0) {
                items.put(medicineId, cappedQuantity);
            } else {
                items.remove(medicineId);
            }
        });
    }

    public void removeItem(Long medicineId) {
        items.remove(medicineId);
    }

    public List<CartItemView> getItems() {
        List<CartItemView> cartItems = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            medicineService.getById(entry.getKey()).ifPresent(medicine -> {
                int quantity = Math.min(entry.getValue(), medicine.getStockQuantity());
                if (quantity > 0) {
                    BigDecimal subtotal = medicine.getPrice().multiply(BigDecimal.valueOf(quantity));
                    cartItems.add(new CartItemView(medicine, quantity, subtotal));
                }
            });
        }

        return cartItems;
    }

    public BigDecimal getTotalCost() {
        return getItems().stream()
                .map(CartItemView::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTotalItems() {
        return getItems().stream()
                .mapToInt(CartItemView::getQuantity)
                .sum();
    }

    public boolean isEmpty() {
        return getItems().isEmpty();
    }
}
