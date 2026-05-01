
package com.project.ecommerce.dto;

import java.util.List;

public class CheckoutResponse {

    public String shippingAddress;
    public CheckoutInfo checkoutInfo;
    public List<CartItem> cartItems;

    public static class CheckoutInfo {
        public int deliverDays;
        public String deliverDate;
        public boolean codSupported;
        public double productTotal;
        public double shippingCostTotal;
        public double paymentTotal;
    }

    public static class CartItem {
        public Long productId;
        public String shortName;
        public int quantity;
        public double subtotal;
    }
}