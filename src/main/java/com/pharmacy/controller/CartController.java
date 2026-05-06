package com.pharmacy.controller;

import com.pharmacy.service.ShoppingCartService;
import com.pharmacy.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CartController {

    private final ShoppingCartService shoppingCartService;
    private final UserService userService;

    public CartController(ShoppingCartService shoppingCartService, UserService userService) {
        this.shoppingCartService = shoppingCartService;
        this.userService = userService;
    }

    @GetMapping("/cart")
    public String cart(Model model, Authentication authentication) {
        model.addAttribute("cartItems", shoppingCartService.getItems());
        model.addAttribute("cartTotal", shoppingCartService.getTotalCost());
        model.addAttribute("userName", resolveUserName(authentication));
        model.addAttribute("role", resolveRole(authentication));
        model.addAttribute("activePage", "cart");
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("medicineId") Long medicineId,
                            @RequestParam(name = "quantity", defaultValue = "1") int quantity,
                            @RequestParam(name = "redirectTo", defaultValue = "/medicines") String redirectTo) {
        shoppingCartService.addItem(medicineId, quantity);
        return "redirect:" + redirectTo;
    }

    @PostMapping("/cart/{medicineId}/update")
    public String updateCartItem(@PathVariable Long medicineId,
                                 @RequestParam("quantity") int quantity) {
        shoppingCartService.updateItem(medicineId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/{medicineId}/remove")
    public String removeCartItem(@PathVariable Long medicineId) {
        shoppingCartService.removeItem(medicineId);
        return "redirect:/cart";
    }

    private String resolveUserName(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "Guest User";
        }

        return userService.findByEmail(authentication.getName())
                .map(user -> user.getFullName())
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
