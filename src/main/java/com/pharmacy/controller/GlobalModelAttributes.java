package com.pharmacy.controller;

import com.pharmacy.service.ShoppingCartService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.math.BigDecimal;

@ControllerAdvice
public class GlobalModelAttributes {

    private final ShoppingCartService shoppingCartService;

    public GlobalModelAttributes(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @ModelAttribute("cartItemCount")
    public int cartItemCount() {
        return shoppingCartService.getTotalItems();
    }

    @ModelAttribute("cartTotalCost")
    public BigDecimal cartTotalCost() {
        return shoppingCartService.getTotalCost();
    }
}
