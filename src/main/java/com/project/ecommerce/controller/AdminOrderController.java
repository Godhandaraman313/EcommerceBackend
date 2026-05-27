package com.project.ecommerce.controller;

import com.project.ecommerce.dto.AdminOrderDetailResponse;
import com.project.ecommerce.dto.AdminOrderSummaryResponse;
import com.project.ecommerce.dto.UpdateOrderStatusRequest;
import com.project.ecommerce.service.AdminOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
@CrossOrigin("http://localhost:5173")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    /** Shopme BackEnd: orders list for admin/shipper */
    @GetMapping
    public Page<AdminOrderSummaryResponse> listOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword
    ) {
        return adminOrderService.listAll(
                keyword,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
    }

    @GetMapping("/{id}")
    public AdminOrderDetailResponse getOrder(@PathVariable Long id) {
        return adminOrderService.getDetail(id);
    }

    /** Shopme: POST /orders_shipper/update/{id}/{status} */
    @PatchMapping("/{id}/status")
    public AdminOrderSummaryResponse updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateOrderStatusRequest request
    ) {
        return adminOrderService.updateStatus(id, request);
    }
}
