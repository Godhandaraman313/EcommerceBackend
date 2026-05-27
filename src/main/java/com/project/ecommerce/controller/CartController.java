package com.project.ecommerce.controller;

import com.project.ecommerce.dto.AddToCartRequest;
import com.project.ecommerce.dto.CartItemResponse;
import com.project.ecommerce.dto.UpdateCartQuantityRequest;
import com.project.ecommerce.model.CartItem;
import com.project.ecommerce.service.CartService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin("http://localhost:5173")
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @PostMapping("/add/{id}")
    public CartItemResponse addByPath(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String email = authentication.getName();
        CartItem item = service.addToCart(id, email, 1);
        return new CartItemResponse(item);
    }

    @PostMapping("/add")
    public CartItemResponse add(
            @RequestBody AddToCartRequest request,
            Authentication authentication
    ) {
        if (request.getProductId() == null) {
            throw new RuntimeException("productId is required");
        }

        int quantity = request.getQuantity() == null ? 1 : request.getQuantity();
        String email = authentication.getName();
        CartItem item = service.addToCart(request.getProductId(), email, quantity);
        return new CartItemResponse(item);
    }

    @GetMapping
    public List<CartItemResponse> getCart(Authentication authentication) {
        return service.getCart(authentication.getName()).stream()
                .map(CartItemResponse::new)
                .toList();
    }

    @GetMapping("/total")
    public Map<String, Double> getTotal(Authentication authentication) {
        return Map.of("total", service.getTotal(authentication.getName()));
    }

    /** Shopme quantity control: set exact quantity */
    @PutMapping("/items/{cartItemId}/quantity")
    public CartItemResponse updateQuantity(
            @PathVariable Long cartItemId,
            @RequestBody UpdateCartQuantityRequest request,
            Authentication authentication
    ) {
        CartItem item = service.updateQuantity(cartItemId, authentication.getName(), request.getQuantity());
        if (item == null) {
            return null;
        }
        return new CartItemResponse(item);
    }

    @DeleteMapping("/items/{cartItemId}")
    public Map<String, String> removeItem(
            @PathVariable Long cartItemId,
            Authentication authentication
    ) {
        service.removeItem(cartItemId, authentication.getName());
        return Map.of("message", "Item removed");
    }

    @DeleteMapping("/clear")
    public Map<String, String> clear(Authentication authentication) {
        service.clearCart(authentication.getName());
        return Map.of("message", "Cart cleared");
    }
}
