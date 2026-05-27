package com.project.ecommerce.service;

import com.project.ecommerce.dto.OrderDetailResponse;
import com.project.ecommerce.dto.OrderItemResponse;
import com.project.ecommerce.dto.OrderReturnRequest;
import com.project.ecommerce.dto.OrderSummaryResponse;
import com.project.ecommerce.dto.OrderTrackResponse;
import com.project.ecommerce.model.Order;
import com.project.ecommerce.model.OrderItem;
import com.project.ecommerce.model.OrderTrack;
import com.project.ecommerce.repository.OrderItemRepository;
import com.project.ecommerce.repository.OrderRepository;
import com.project.ecommerce.repository.OrderTrackRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderTrackRepository orderTrackRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            OrderTrackRepository orderTrackRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderTrackRepository = orderTrackRepository;
    }

    public Page<OrderSummaryResponse> listForCustomer(String email, String keyword, Pageable pageable) {
        Page<Order> page;

        if (keyword != null && !keyword.isBlank()) {
            page = searchByKeyword(email, keyword.trim(), pageable);
        } else {
            page = orderRepository.findByUserEmailOrderByCreatedAtDesc(email, pageable);
        }

        return page.map(order -> {
            List<String> names = orderItemRepository.findByOrder_IdOrderByIdAsc(order.getId()).stream()
                    .map(OrderItem::getProductName)
                    .toList();
            return OrderSummaryResponse.from(order, names);
        });
    }

    public OrderDetailResponse getOrderDetail(Long orderId, String email) {
        Order order = orderRepository.findByIdAndUserEmail(orderId, email)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<OrderItemResponse> items = orderItemRepository.findByOrder_IdOrderByIdAsc(orderId).stream()
                .map(OrderItemResponse::from)
                .toList();

        List<OrderTrackResponse> tracks = orderTrackRepository.findByOrder_IdOrderByCreatedAtAsc(orderId).stream()
                .map(OrderTrackResponse::from)
                .toList();

        return OrderDetailResponse.from(order, items, tracks);
    }

    @Transactional
    public OrderDetailResponse requestReturn(Long orderId, String email, OrderReturnRequest request) {
        if (request.getReason() == null || request.getReason().isBlank()) {
            throw new RuntimeException("Return reason is required");
        }

        Order order = orderRepository.findByIdAndUserEmail(orderId, email)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!"DELIVERED".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("Only delivered orders can be returned");
        }

        if (order.isReturnRequested()) {
            throw new RuntimeException("Return already requested for this order");
        }

        order.setReturnRequested(true);
        order.setReturnReason(request.getReason().trim());
        order.setReturnNote(request.getNote() != null ? request.getNote().trim() : null);
        order.setStatus("RETURN_REQUESTED");

        OrderTrack track = new OrderTrack();
        track.setOrder(order);
        track.setStatus("RETURN_REQUESTED");
        String notes = "Reason: " + request.getReason();
        if (request.getNote() != null && !request.getNote().isBlank()) {
            notes += ". " + request.getNote();
        }
        track.setNotes(notes);
        orderTrackRepository.save(track);

        orderRepository.save(order);

        return getOrderDetail(orderId, email);
    }

    private Page<Order> searchByKeyword(String email, String keyword, Pageable pageable) {
        try {
            Long orderId = Long.parseLong(keyword);
            Optional<Order> match = orderRepository.findByIdAndUserEmail(orderId, email);
            if (match.isEmpty()) {
                return new PageImpl<>(Collections.emptyList(), pageable, 0);
            }
            return new PageImpl<>(List.of(match.get()), pageable, 1);
        } catch (NumberFormatException ex) {
            return orderRepository.findByUserEmailOrderByCreatedAtDesc(email, pageable);
        }
    }
}
