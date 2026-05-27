package com.project.ecommerce.controller;

import com.project.ecommerce.dto.OrderDetailResponse;
import com.project.ecommerce.dto.OrderReturnRequest;
import com.project.ecommerce.dto.OrderSummaryResponse;
import com.project.ecommerce.dto.PlaceOrderRequest;
import com.project.ecommerce.service.CheckoutService;
import com.project.ecommerce.service.OrderService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin("http://localhost:5173")
public class OrderController {

    private final CheckoutService checkoutService;
    private final OrderService orderService;

    public OrderController(CheckoutService checkoutService, OrderService orderService) {
        this.checkoutService = checkoutService;
        this.orderService = orderService;
    }

    /** Shopme: GET /orders — customer's order history */
    @GetMapping
    public Page<OrderSummaryResponse> listOrders(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword
    ) {
        return orderService.listForCustomer(
                authentication.getName(),
                keyword,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
    }

    /** Shopme: GET /orders/detail/{id} */
    @GetMapping("/{id}")
    public OrderDetailResponse getOrder(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return orderService.getOrderDetail(id, authentication.getName());
    }

    /** Shopme: POST /orders/return */
    @PostMapping("/{id}/return")
    public OrderDetailResponse requestReturn(
            @PathVariable Long id,
            @RequestBody OrderReturnRequest request,
            Authentication authentication
    ) {
        return orderService.requestReturn(id, authentication.getName(), request);
    }

    @PostMapping
    public Map<String, Object> placeOrder(
            @RequestBody PlaceOrderRequest request,
            Authentication authentication
    ) {
        return checkoutService.placeOrder(authentication.getName(), request.getMethod());
    }
}
