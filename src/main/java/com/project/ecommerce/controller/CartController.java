package com.project.ecommerce.controller;

import com.project.ecommerce.model.CartItem;
import com.project.ecommerce.service.CartService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin("*")
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @PostMapping("/add/{id}")
    public CartItem add(@PathVariable Long id) {
        return service.addToCart(id);
    }

    @GetMapping
    public List<CartItem> getCart() {
        return service.getCart();
    }

    @GetMapping("/total")
    public double getTotal() {
        return service.getTotal();
    }

    @DeleteMapping("/clear")
    public void clear() {
        service.clearCart();
    }
}