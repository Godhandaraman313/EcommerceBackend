
package com.project.ecommerce.controller;

import com.project.ecommerce.dto.CheckoutResponse;
import com.project.ecommerce.service.CheckoutService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
@CrossOrigin("http://localhost:5173")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @GetMapping
    public CheckoutResponse getCheckout(Authentication authentication) {
        return checkoutService.buildCheckout(authentication.getName());
    }
}
