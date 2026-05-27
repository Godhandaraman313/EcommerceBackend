package com.project.ecommerce.service;

import com.project.ecommerce.dto.*;
import com.project.ecommerce.model.Order;
import com.project.ecommerce.model.OrderStatus;
import com.project.ecommerce.model.OrderTrack;
import com.project.ecommerce.model.User;
import com.project.ecommerce.repository.OrderItemRepository;
import com.project.ecommerce.repository.OrderRepository;
import com.project.ecommerce.repository.OrderTrackRepository;
import com.project.ecommerce.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderTrackRepository orderTrackRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public AdminOrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            OrderTrackRepository orderTrackRepository,
            UserRepository userRepository,
            EmailService emailService
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderTrackRepository = orderTrackRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public Page<AdminOrderSummaryResponse> listAll(String keyword, Pageable pageable) {
        Page<Order> page;

        if (keyword != null && !keyword.isBlank()) {
            page = searchOrders(keyword.trim(), pageable);
        } else {
            page = orderRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        return page.map(this::toSummary);
    }

    public AdminOrderDetailResponse getDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        User customer = userRepository.findByEmail(order.getUserEmail()).orElse(null);
        String recipientName = buildRecipientName(customer);
        String phone = customer != null ? customer.getPhoneNumber() : null;

        List<OrderItemResponse> items = orderItemRepository.findByOrder_IdOrderByIdAsc(orderId).stream()
                .map(OrderItemResponse::from)
                .toList();

        List<OrderTrackResponse> tracks = orderTrackRepository.findByOrder_IdOrderByCreatedAtAsc(orderId).stream()
                .map(OrderTrackResponse::from)
                .toList();

        return AdminOrderDetailResponse.from(order, items, tracks, recipientName, phone);
    }

    @Transactional
    public AdminOrderSummaryResponse updateStatus(Long orderId, UpdateOrderStatusRequest request) {
        if (request.getStatus() == null || request.getStatus().isBlank()) {
            throw new RuntimeException("Status is required");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        String newStatus;
        try {
            newStatus = OrderStatus.fromString(request.getStatus()).name();
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid order status: " + request.getStatus());
        }

        if (newStatus.equalsIgnoreCase(order.getStatus())) {
            return toSummary(order);
        }

        order.setStatus(newStatus);
        orderRepository.save(order);

        OrderTrack track = new OrderTrack();
        track.setOrder(order);
        track.setStatus(newStatus);
        track.setNotes(request.getNotes() != null ? request.getNotes() : defaultNote(newStatus));
        orderTrackRepository.save(track);

        try {
            emailService.sendOrderStatusUpdateEmail(
                    order.getUserEmail(),
                    String.valueOf(order.getId()),
                    newStatus,
                    track.getNotes()
            );
        } catch (Exception e) {
            System.err.println("Failed to send order status update email: " + e.getMessage());
        }

        return toSummary(order);
    }

    private Page<Order> searchOrders(String keyword, Pageable pageable) {
        try {
            Long orderId = Long.parseLong(keyword);
            Optional<Order> byId = orderRepository.findById(orderId);
            if (byId.isPresent()) {
                return new PageImpl<>(List.of(byId.get()), pageable, 1);
            }
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        } catch (NumberFormatException ex) {
            return orderRepository.findByUserEmailContainingIgnoreCaseOrderByCreatedAtDesc(keyword, pageable);
        }
    }

    private AdminOrderSummaryResponse toSummary(Order order) {
        List<String> names = orderItemRepository.findByOrder_IdOrderByIdAsc(order.getId()).stream()
                .map(item -> item.getProductName())
                .toList();

        User customer = userRepository.findByEmail(order.getUserEmail()).orElse(null);
        return AdminOrderSummaryResponse.from(
                order,
                names,
                buildRecipientName(customer),
                customer != null ? customer.getPhoneNumber() : null
        );
    }

    private String buildRecipientName(User user) {
        if (user == null) return "Customer";
        String first = user.getFirstName() != null ? user.getFirstName().trim() : "";
        String last = user.getLastName() != null ? user.getLastName().trim() : "";
        String full = (first + " " + last).trim();
        if (!full.isEmpty()) return full;
        return user.getUsername() != null ? user.getUsername() : user.getEmail();
    }

    private String defaultNote(String status) {
        return switch (status) {
            case "PROCESSING" -> "Order is being processed";
            case "PICKED" -> "Shipper picked the package";
            case "SHIPPING" -> "Shipper is delivering the package";
            case "DELIVERED" -> "Customer received products";
            case "CANCELLED" -> "Order was cancelled";
            case "RETURNED" -> "Products were returned";
            default -> "Status updated to " + status;
        };
    }
}
