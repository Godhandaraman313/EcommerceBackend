package com.project.ecommerce.service;

import com.project.ecommerce.dto.CheckoutResponse;
import com.project.ecommerce.model.Address;
import com.project.ecommerce.model.CartItem;
import com.project.ecommerce.model.Order;
import com.project.ecommerce.model.OrderItem;
import com.project.ecommerce.model.User;
import com.project.ecommerce.repository.AddressRepository;
import com.project.ecommerce.model.OrderTrack;
import com.project.ecommerce.repository.OrderItemRepository;
import com.project.ecommerce.repository.OrderRepository;
import com.project.ecommerce.repository.OrderTrackRepository;
import com.project.ecommerce.service.CartService;
import com.project.ecommerce.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class CheckoutService {

    private static final double SHIPPING_COST = 50.0;

    private final CartService cartService;
    private final EmailService emailService;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderTrackRepository orderTrackRepository;

    public CheckoutService(
            CartService cartService,
            AddressRepository addressRepository,
            UserRepository userRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            OrderTrackRepository orderTrackRepository,
            EmailService emailService) {
        this.cartService = cartService;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderTrackRepository = orderTrackRepository;
        this.emailService = emailService;
    }

    public CheckoutResponse buildCheckout(String userEmail) {
        List<CartItem> cart = cartService.getCart(userEmail);
        if (cart.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        CheckoutResponse response = new CheckoutResponse();
        response.shippingAddress = resolveShippingAddress(userEmail);

        double productTotal = cartService.getTotal(userEmail);
        CheckoutResponse.CheckoutInfo info = new CheckoutResponse.CheckoutInfo();
        info.deliverDays = 3;
        info.deliverDate = "3–5 business days";
        info.codSupported = true;
        info.productTotal = productTotal;
        info.shippingCostTotal = SHIPPING_COST;
        info.paymentTotal = productTotal + SHIPPING_COST;
        response.checkoutInfo = info;

        List<CheckoutResponse.CartLineItem> lines = new ArrayList<>();
        for (CartItem item : cart) {
            CheckoutResponse.CartLineItem line = new CheckoutResponse.CartLineItem();
            line.quantity = item.getQuantity();
            line.subtotal = item.getProduct().getPrice() * item.getQuantity();

            CheckoutResponse.ProductRef product = new CheckoutResponse.ProductRef();
            product.id = item.getProduct().getId();
            product.shortName = item.getProduct().getName();
            product.imageUrl = item.getProduct().getImageUrl();
            line.product = product;

            lines.add(line);
        }
        response.cartItems = lines;

        return response;
    }

    @Transactional
    public Map<String, Object> placeOrder(String userEmail, String paymentMethod) {
        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new RuntimeException("Payment method is required");
        }

        List<CartItem> cart = cartService.getCart(userEmail);
        if (cart.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        double productTotal = cartService.getTotal(userEmail);
        double total = productTotal + SHIPPING_COST;

        Order order = new Order();
        order.setUserEmail(userEmail);
        order.setPaymentMethod(paymentMethod);
        order.setTotal(total);
        order.setStatus("NEW");
        order.setShippingAddress(resolveShippingAddress(userEmail));
        order = orderRepository.save(order);

        OrderTrack initialTrack = new OrderTrack();
        initialTrack.setOrder(order);
        initialTrack.setStatus("NEW");
        initialTrack.setNotes("Order was placed by the customer");
        orderTrackRepository.save(initialTrack);

        List<OrderItem> savedItems = new ArrayList<>();
        for (CartItem cartItem : cart) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(cartItem.getProduct().getId());
            orderItem.setProductName(cartItem.getProduct().getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItemRepository.save(orderItem);
            savedItems.add(orderItem);
        }

        cartService.clearCart(userEmail);

        // Send order confirmation email with product details
        try {
            emailService.sendOrderConfirmationEmail(userEmail, String.valueOf(order.getId()), savedItems);
        } catch (Exception e) {
            System.err.println("Failed to send order confirmation email: " + e.getMessage());
        }
        return Map.of(
                "message", "Order placed",
                "orderId", order.getId(),
                "total", total
        );
    }

    private String resolveShippingAddress(String userEmail) {
        List<Address> addresses = addressRepository.findByEmail(userEmail);

        Address chosen = addresses.stream()
                .filter(Address::isDefaultForShipping)
                .findFirst()
                .orElse(addresses.isEmpty() ? null : addresses.get(0));

        if (chosen != null) {
            return String.format(
                    "%s %s, %s, %s, %s %s",
                    chosen.getFirstName(),
                    chosen.getLastName(),
                    chosen.getAddressLine1(),
                    chosen.getCity(),
                    chosen.getCountry(),
                    chosen.getPostalCode()
            ).trim();
        }

        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user != null && user.getAddressLine1() != null && !user.getAddressLine1().isBlank()) {
            return String.format(
                    "%s, %s, %s %s",
                    user.getAddressLine1(),
                    user.getCity(),
                    user.getCountry(),
                    user.getPostalCode()
            ).trim();
        }

        return "No shipping address on file";
    }
}
