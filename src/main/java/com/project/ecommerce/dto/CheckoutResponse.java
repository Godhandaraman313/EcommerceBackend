package com.project.ecommerce.dto;

import java.util.List;

public class CheckoutResponse {

    public String shippingAddress;
    public CheckoutInfo checkoutInfo;
    public List<CartLineItem> cartItems;

    public static class CheckoutInfo {
        public int deliverDays;
        public String deliverDate;
        public boolean codSupported;
        public double productTotal;
        public double shippingCostTotal;
        public double paymentTotal;
    }

    /** Matches frontend: item.product.id, item.product.shortName, item.quantity */
    public static class CartLineItem {
        public int quantity;
        public double subtotal;
        public ProductRef product;
    }

    public static class ProductRef {
        public Long id;
        public String shortName;
        public String imageUrl;
    }
}
