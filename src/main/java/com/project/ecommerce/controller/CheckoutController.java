
package com.project.ecommerce.controller;

import com.project.ecommerce.dto.CheckoutResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkout")
@CrossOrigin("http://localhost:5173")
public class CheckoutController {

    @GetMapping
    public CheckoutResponse getCheckout() {

        CheckoutResponse res = new CheckoutResponse();

        res.shippingAddress = "Demo Address";

        CheckoutResponse.CheckoutInfo info = new CheckoutResponse.CheckoutInfo();
        info.deliverDays = 3;
        info.deliverDate = "Tomorrow";
        info.codSupported = true;
        info.productTotal = 1000;
        info.shippingCostTotal = 50;
        info.paymentTotal = 1050;

        res.checkoutInfo = info;

        res.cartItems = List.of(); // keep empty or mock

        return res;
    }
}