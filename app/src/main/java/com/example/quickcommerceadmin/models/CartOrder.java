package com.example.quickcommerceadmin.models;

import java.util.List;

public class CartOrder {

    // ✅ Inner class for CartItem
    public static class CartItem {
        private String productId;
        private String title;
        private int quantity;
        private double price; // Change price to double for flexibility with cents
        private String imageUrl;  // Store only one image URL for the cart item

        public CartItem() {
            // Default constructor required for calls to DataSnapshot.getValue(CartItem.class)
        }

        // Constructor with image URL
        public CartItem(String productId, String title, int quantity, double price, String imageUrl) {
            this.productId = productId;
            this.title = title;
            this.quantity = quantity;
            this.price = price;
            this.imageUrl = imageUrl;
        }

        // Getters and setters
        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }


    // ✅ Inner class for Order
    public static class Order {
        private String orderId;
        private String userId;
        private List<CartItem> cartItems;
        private double totalAmount;
        private int orderStatus;
        private String orderDate;
        private String userAddress;  // ✅ New field

        // Required by Firebase
        public Order() {}

        // Full constructor including userAddress
        public Order(String orderId, String userId, List<CartItem> cartItems, double totalAmount,
                     int orderStatus, String orderDate, String userAddress) {
            this.orderId = orderId;
            this.userId = userId;
            this.cartItems = cartItems;
            this.totalAmount = totalAmount;
            this.orderStatus = orderStatus;
            this.orderDate = orderDate;
            this.userAddress = userAddress;  // ✅ Set new field
        }

        // Getters and Setters
        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public List<CartItem> getCartItems() {
            return cartItems;
        }

        public void setCartItems(List<CartItem> cartItems) {
            this.cartItems = cartItems;
            this.totalAmount = calculateTotalAmount();
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }

        public long getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(int orderStatus) {
            this.orderStatus = orderStatus;
        }

        public String getOrderDate() {
            return orderDate;
        }

        public void setOrderDate(String orderDate) {
            this.orderDate = orderDate;
        }

        // ✅ Getter for userAddress
        public String getUserAddress() {
            return userAddress;
        }

        // ✅ Setter for userAddress
        public void setUserAddress(String userAddress) {
            this.userAddress = userAddress;
        }

        // Helper to calculate total
        private double calculateTotalAmount() {
            double total = 0;
            if (cartItems != null) {
                for (CartItem item : cartItems) {
                    total += item.getPrice() * item.getQuantity();
                }
            }
            return total;
        }
    }


}
