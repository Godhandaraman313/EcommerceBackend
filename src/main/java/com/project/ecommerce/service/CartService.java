package com.project.ecommerce.service;

import com.project.ecommerce.model.CartItem;
import com.project.ecommerce.model.Product;
import com.project.ecommerce.repository.CartRepository;
import com.project.ecommerce.repository.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepo;
    private final ProductRepository productRepo;

    public CartService(CartRepository cartRepo, ProductRepository productRepo) {
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
    }

    public CartItem addToCart(Long productId, String userEmail, int quantity) {
        if (quantity < 1) {
            throw new RuntimeException("Quantity must be at least 1");
        }

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return cartRepo.findByUserEmailAndProduct_Id(userEmail, productId)
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + quantity);
                    return cartRepo.save(existing);
                })
                .orElseGet(() -> {
                    CartItem item = new CartItem();
                    item.setProduct(product);
                    item.setQuantity(quantity);
                    item.setUserEmail(userEmail);
                    return cartRepo.save(item);
                });
    }

    public List<CartItem> getCart(String userEmail) {
        return cartRepo.findByUserEmail(userEmail);
    }

    public double getTotal(String userEmail) {
        return cartRepo.findByUserEmail(userEmail).stream()
                .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();
    }

    public void clearCart(String userEmail) {
        cartRepo.deleteByUserEmail(userEmail);
    }

    public CartItem updateQuantity(Long cartItemId, String userEmail, int quantity) {
        CartItem item = cartRepo.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!userEmail.equals(item.getUserEmail())) {
            throw new RuntimeException("Cart item not found");
        }

        if (quantity < 1) {
            cartRepo.delete(item);
            return null;
        }

        item.setQuantity(quantity);
        return cartRepo.save(item);
    }

    public void removeItem(Long cartItemId, String userEmail) {
        CartItem item = cartRepo.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!userEmail.equals(item.getUserEmail())) {
            throw new RuntimeException("Cart item not found");
        }

        cartRepo.delete(item);
    }
}
