package com.pharmacy.model;

import java.math.BigDecimal;

public class CartItemView {

    private final Medicine medicine;
    private final int quantity;
    private final BigDecimal subtotal;

    public CartItemView(Medicine medicine, int quantity, BigDecimal subtotal) {
        this.medicine = medicine;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}
